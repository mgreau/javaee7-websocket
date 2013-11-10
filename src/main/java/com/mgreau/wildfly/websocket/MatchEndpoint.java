package com.mgreau.wildfly.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.mgreau.wildfly.websocket.decoders.MessageDecoder;
import com.mgreau.wildfly.websocket.encoders.BetMessageEncoder;
import com.mgreau.wildfly.websocket.encoders.MatchMessageEncoder;
import com.mgreau.wildfly.websocket.messages.BetMessage;
import com.mgreau.wildfly.websocket.messages.MatchMessage;

@ServerEndpoint(
		value = "/matches/{match-id}",
		        decoders = { MessageDecoder.class }, 
		        encoders = { MatchMessageEncoder.class, BetMessageEncoder.class }
		)
public class MatchEndpoint {
	
	/** log */
	private static final Logger logger = Logger.getLogger("MatchEndpoint");
	
    /** All open WebSocket sessions */
    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    
    /** Handle number of bets by match */
    static Map<String, AtomicInteger> nbBetsByMatch = new ConcurrentHashMap<>();
    
    @Inject StarterService ejbService;
    
    /**
     * Send Live Match message for all peers connected to this match
     * @param msg
     * @param matchId
     */
    public static void send(MatchMessage msg, String matchId) {
        try {
            /* Send updates to all open WebSocket sessions for this match */
            for (Session session : peers) {
            	if (Boolean.TRUE.equals(session.getUserProperties().get(matchId))){
            		if (session.isOpen()){
	            		session.getBasicRemote().sendObject(msg);
	                    logger.log(Level.INFO, " Score Sent: {0}", msg);
            		}
            	}
            }
        } catch (IOException | EncodeException e) {
            logger.log(Level.INFO, e.toString());
        }   
    }
    
    /**
     * When the match is finished, each peer which has bet on this match receive a message.
     * @param winner
     * @param matchId
     */
    public static void sendBetMessages(String winner, String matchId, boolean isFinished) {
        try {
            /* Send updates to all open WebSocket sessions for this match */
            for (Session session : peers) {
            	if (Boolean.TRUE.equals(session.getUserProperties().get(matchId))){
            		if (session.isOpen()){
            			if (session.getUserProperties().containsKey("bet")){
            				BetMessage betMsg = new BetMessage((String)session.getUserProperties().get("bet"));
            				if (isFinished){
	            				if (winner != null 
		            					&& winner.equals(betMsg.getWinner())){
		            				betMsg.setResult("OK");
		            			} else {
		            				betMsg.setResult("KO");
		            			}
            				}
            				sendBetMessage(session, betMsg, matchId);
		                    logger.log(Level.INFO, "Result Sent: {0}", betMsg.getResult());
            			}
        			}
            		if (isFinished){
	        			//Match finished, need to clear properties
	            		session.getUserProperties().clear();
	            		nbBetsByMatch.get(matchId).set(0);
            		}
        		}
            }
            logger.log(Level.INFO, "Match FINISHED");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
        }   
    }
    
    
    public static void sendBetMessage(Session session, BetMessage betMsg, String matchId) {
        try {
        	betMsg.setNbBets(nbBetsByMatch.get(matchId).get());
    		session.getBasicRemote().sendObject(betMsg);
            logger.log(Level.INFO, "BetMsg Sent: {0}", betMsg.toString());
        } catch (IOException | EncodeException e) {
            logger.log(Level.SEVERE, e.toString());
        }   
    }
    
    
    @OnMessage
    public void message(final Session session, BetMessage msg,  @PathParam("match-id") String matchId) {
        logger.log(Level.INFO, "Received: Bet Match Winner - {0}", msg.getWinner());
        //check if the user had already bet and save this bet
        boolean hasAlreadyBet = session.getUserProperties().containsKey("bet");
        session.getUserProperties().put("bet", msg.getWinner());
        
        //Send betMsg with bet count
        if (!nbBetsByMatch.containsKey(matchId)){
        	nbBetsByMatch.put(matchId, new AtomicInteger());
        }
        if (!hasAlreadyBet){
        	nbBetsByMatch.get(matchId).incrementAndGet();
        }
        sendBetMessages(null, matchId, false);
    }

    @OnOpen
    public void openConnection(Session session, @PathParam("match-id") String matchId) {
    	logger.log(Level.INFO, "Session ID : " + session.getId() +" - Connection opened for match : " + matchId);
        session.getUserProperties().put(matchId, true);
        peers.add(session);
       
        //Send live result for this match
        send(new MatchMessage(ejbService.getMatches().get(matchId)), matchId);
    }
    
    @OnClose
    public void closedConnection(Session session, @PathParam("match-id") String matchId) {
    	if (session.getUserProperties().containsKey("bet")){
            /* Remove bet */
    		 nbBetsByMatch.get(matchId).decrementAndGet();
    		 sendBetMessages(null, matchId, false);
    	}
        /* Remove this connection from the queue */
        peers.remove(session);
        logger.log(Level.INFO, "Connection closed.");
    }
    
    @OnError
    public void error(Session session, Throwable t) {
        peers.remove(session);
        logger.log(Level.INFO, t.toString());
        logger.log(Level.INFO, "Connection error.");
    }
   
}
