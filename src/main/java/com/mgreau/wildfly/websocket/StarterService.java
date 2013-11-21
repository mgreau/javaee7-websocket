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

    private Random random;
    private Map<String, TennisMatch> matches = new ConcurrentHashMap<>();
    
    private static final Logger logger = Logger.getLogger("StarterService");
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "Initializing App.");
        random = new Random();
        matches.put("1234", new TennisMatch("1234", "US OPEN - QUARTER FINALS", "Ferrer D.", "es", "Almagro N.", "es"));
        matches.put("1235", new TennisMatch("1235", "US OPEN - QUARTER FINALS", "Djokovic N.", "rs", "Berdych T.", "cz"));
        matches.put("1236", new TennisMatch("1236", "US OPEN - QUARTER FINALS", "Murray A.", "gb", "Chardy J.", "fr"));
        matches.put("1237", new TennisMatch("1237", "US OPEN - QUARTER FINALS", "Federer R.", "ch", "Tsonga J.W.", "fr"));
    }
    
    @Schedule(second="*/3", minute="*",hour="*", persistent=false)
    public void play() {
    	for (Map.Entry<String,TennisMatch> match : matches.entrySet()){
    		TennisMatch m = match.getValue();
    		if (m.isFinished()){
    			//add a timer to restart a match after 20 secondes
    			m.reset();
    		}
        	//Handle point
    		if (random.nextInt(2) == 1){
        		m.playerOneScores();
        	} else {
        		m.playerTwoScores();
        	}
        	MatchEndpoint.send(new MatchMessage(m), match.getKey());
        	//if there is a winner, send result and reset the game
        	if (m.isFinished()){
        		MatchEndpoint.sendBetMessages(m.playerWithHighestSets(), match.getKey(), true);
        	}
    	}
    }
    
    public Map<String, TennisMatch> getMatches(){
    	return matches;
    }
}
