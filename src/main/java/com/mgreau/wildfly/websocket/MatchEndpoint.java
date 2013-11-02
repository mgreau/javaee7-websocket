package com.mgreau.wildfly.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	
	private static final Logger logger = Logger.getLogger("MatchEndpoint");
	
	@Inject
	StarterService ejbService;
	
    /* Queue for all open WebSocket sessions */
    static Queue<Session> queue = new ConcurrentLinkedQueue<>();
    
    static Map<String, Integer> nbBetsByMatch = new ConcurrentHashMap<>();
    
    public static void send(MatchMessage msg, String matchId) {
        try {
            /* Send updates to all open WebSocket sessions for this match */
            for (Session session : queue) {
            	if (Boolean.TRUE.equals(session.getUserProperties().get(matchId))){
            		if (session.isOpen()){
            			BetMessage bet =  (BetMessage)session.getUserProperties().get("betMatchWinner"+matchId);
            			if (bet != null){
            				msg.setBetOn(bet.getWinner());
            			}
            			if (nbBetsByMatch.get(matchId) != null)
            				msg.setNbBets(nbBetsByMatch.get(matchId));
	            		session.getBasicRemote().sendObject(msg);
	                    logger.log(Level.INFO, "Score Sent: {0}", msg);
            		}
            	}
            }
        } catch (IOException | EncodeException e) {
            logger.log(Level.INFO, e.toString());
        }   
    }
    
    public static void sendBetResult(String winner, String matchId) {
        try {
            /* Send updates to all open WebSocket sessions for this match */
            for (Session session : queue) {
            	if (Boolean.TRUE.equals(session.getUserProperties().get(matchId))){
            		if (session.isOpen()){
            			BetMessage msg =  (BetMessage)session.getUserProperties().get("betMatchWinner"+matchId);
            			if (winner != null && winner.equals(msg.getWinner())){
            				msg.setResult("OK");
            			} else {
            				msg.setResult("KO");
            			}
	            		session.getBasicRemote().sendObject(msg);
	                    logger.log(Level.INFO, "Result Sent: {0}", msg.getResult());
            		}
            	}
            }
        } catch (IOException | EncodeException e) {
            logger.log(Level.INFO, e.toString());
        }   
    }
    
    
    @OnMessage
    public void message(final Session session, BetMessage msg) {
        logger.log(Level.INFO, "Received: Bet Match Winner - {0}", msg.getWinner());
        //save this bet
        session.getUserProperties().put("betMatchWinner"+msg.getMatchKey(), msg);
        incrementBetOnMatch(msg.getMatchKey());
        
        //Send live result for this match
        MatchMessage matchMsg = new MatchMessage(ejbService.getMatches().get(msg.getMatchKey()));
        send(matchMsg, msg.getMatchKey());
    }

    @OnOpen
    public void openConnection(Session session, @PathParam("match-id") String gameId) {
        queue.add(session);
        session.getUserProperties().put(gameId, true);
        logger.log(Level.INFO, "Connection opened for game : " + gameId);
        //Send live result for this match
        send(new MatchMessage(ejbService.getMatches().get(gameId)), gameId);
    }
    
    @OnClose
    public void closedConnection(Session session) {
        /* Remove this connection from the queue */
        queue.remove(session);
        logger.log(Level.INFO, "Connection closed.");
    }
    
    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        logger.log(Level.INFO, t.toString());
        logger.log(Level.INFO, "Connection error.");
    }
    
    private void incrementBetOnMatch(String matchKey){
    	if (nbBetsByMatch.get(matchKey) == null)
    		nbBetsByMatch.put(matchKey, 0);
    	nbBetsByMatch.put(matchKey,1+nbBetsByMatch.get(matchKey));
    }
}
