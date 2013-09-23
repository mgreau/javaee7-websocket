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
	
	public BetMessage(String winner){
		this.winner = winner;
	}
	
	
	public String getWinner(){
		return winner;
	}
    
	public String toString(){
		return "[BetMessage] ...";
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}
}
