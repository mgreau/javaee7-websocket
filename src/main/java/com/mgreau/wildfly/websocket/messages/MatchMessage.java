package com.mgreau.wildfly.websocket.messages;

import com.mgreau.wildfly.websocket.TennisMatch;

public class MatchMessage extends Message {

	private TennisMatch match;
	
	public MatchMessage(TennisMatch match) {
		this.match = match;
	}

	public TennisMatch getMatch() {
		return match;
	}

	@Override
	public String toString() {
		return "[MatchMessage] " + match.getKey() + "-" + match.getTitle() + "-"
				+ match.getPlayerOneName() + "-" + match.getPlayerTwoName();
	}

}
