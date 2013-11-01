/**
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package com.mgreau.wildfly.websocket.messages;

public class BetMessage extends Message {
	
	private String winner;
	
	private String result;
	
	private String matchKey;
	
	public BetMessage(String winner, String matchKey){
		this.winner = winner;
		this.matchKey = matchKey;
	}
	
	
	public String getWinner(){
		return winner;
	}
    
	public String toString(){
		return "[BetMessage]["+ matchKey + "] ..." + winner;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public String getMatchKey() {
		return matchKey;
	}


	public void setMatchKey(String matchKey) {
		this.matchKey = matchKey;
	}
	
}
