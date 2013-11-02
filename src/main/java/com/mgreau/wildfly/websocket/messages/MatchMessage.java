package com.mgreau.wildfly.websocket.messages;

import com.mgreau.wildfly.websocket.TennisMatch;

public class MatchMessage extends Message {

	private TennisMatch match;
	
	/** bet on a player ? */
	private String betOn;
	
	/** Number of Bets on this match*/
	private Integer nbBets = 0;

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

	public Integer getNbBets() {
		return nbBets;
	}

	public void setNbBets(Integer nbBets) {
		this.nbBets = nbBets;
	}

}
