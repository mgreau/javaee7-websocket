package com.mgreau.wildfly.websocket;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;

import com.mgreau.wildfly.websocket.messages.MatchMessage;

@Startup
@Singleton
public class StarterService {
    /* Use the container's timer service */
    @Resource TimerService tservice;
    private Random random;
    private Set<TennisMatch> matches;
    
    private static final Logger logger = Logger.getLogger("StarterService");
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "Initializing EJB.");
        random = new Random();
        matches = new LinkedHashSet<>();
        matches.add(new TennisMatch("1234", "US OPEN - FINAL", "N. DJOKOVIC", "R. NADAL"));
    }
    
    @Schedule(second="*/3", minute="*",hour="*", persistent=false)
    public void play() {
    	for (TennisMatch m : matches){
    		if (m.hasMatchWinner()){
    			m.reset();
    			logger.log(Level.INFO, "------- RESET MATCH -----------");
    		}
        	logger.log(Level.INFO, "------- 1 point -----------");
    		if (random.nextInt(2) == 1){
        		m.playerOneScores();
        	} else {
        		m.playerTwoScores();
        	}
        	MatchEndpoint.send(new MatchMessage(m), m.getKey());
        	//if there is a winner, send result and reset the game
        	if (m.hasMatchWinner()){
        		MatchEndpoint.sendBetResult(m.playerWithHighestSets(), m.getKey());
        		logger.log(Level.INFO, "------- MATCH FINISHED -----------");
        	}
    	}
    }
    
    @Timeout
    public void timeout(TimerService tseTimerService){
    	logger.log(Level.INFO, "------- TIMEOUT -----------"+tseTimerService.getTimers().size());
    }
}
