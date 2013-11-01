package com.mgreau.wildfly.websocket;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TimerService;

import com.mgreau.wildfly.websocket.messages.MatchMessage;

@Startup
@Singleton
public class StarterService {
    /* Use the container's timer service */
    @Resource TimerService tservice;
    private Random random;
    private Map<String, TennisMatch> matches = new ConcurrentHashMap<>();
    
    private static final Logger logger = Logger.getLogger("StarterService");
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "Initializing App.");
        random = new Random();
        matches.put("1234", new TennisMatch("1234", "US OPEN - QUARTER FINALS", "Ferrer D.", "Almagro N."));
        matches.put("1235", new TennisMatch("1235", "US OPEN - QUARTER FINALS", "Djokovic N.", "Berdych T."));
        matches.put("1236", new TennisMatch("1236", "US OPEN - QUARTER FINALS", "Murray A.", "Chardy J."));
        matches.put("1237", new TennisMatch("1237", "US OPEN - QUARTER FINALS", "Federer R.", "Tsonga J.W."));
    }
    
    @Schedule(second="*/3", minute="*",hour="*", persistent=false)
    public void play() {
    	for (Map.Entry<String,TennisMatch> match : matches.entrySet()){
    		TennisMatch m = match.getValue();
    		if (m.hasMatchWinner()){
    			m.reset();
    			logger.log(Level.INFO, "---- RESET MATCH ----" + m.getPlayerOneName() 
    					+ " VS " + m.getPlayerTwoName() );
    		}
        	
    		if (random.nextInt(2) == 1){
        		m.playerOneScores();
        	} else {
        		m.playerTwoScores();
        	}
        	MatchEndpoint.send(new MatchMessage(m), match.getKey());
        	//if there is a winner, send result and reset the game
        	if (m.hasMatchWinner()){
        		MatchEndpoint.sendBetResult(m.playerWithHighestSets(), match.getKey());
        		logger.log(Level.INFO, "---- MATCH FINISHED ("+ m.playerWithHighestSets() +" "
        				+ "Wins) ----" + m.getPlayerOneName() + " VS " + m.getPlayerTwoName() );
            	
        	}
    	}
    }
    
    public Map<String, TennisMatch> getMatches(){
    	return matches;
    }
   
}
