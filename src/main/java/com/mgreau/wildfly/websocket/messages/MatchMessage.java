/**
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package com.mgreau.wildfly.websocket.messages;

import com.mgreau.wildfly.websocket.TennisMatch;

public class MatchMessage extends Message {

	private TennisMatch match;
	
	private String betOn;

	public MatchMessage(TennisMatch match) {
		this.match = match;
	}

	public TennisMatch getMatch() {
		return match;
	}
	

	public String getBetOn() {
		return betOn;
	}

	public void setBetOn(String betOn) {
		this.betOn = betOn;
	}

	/* For logging purposes */
	@Override
	public String toString() {
		return "[MatchMessage] " + match.getKey() + "-" + match.getTitle() + "-"
				+ match.getPlayerOneName() + "-" + match.getPlayerTwoName();
	}

}
