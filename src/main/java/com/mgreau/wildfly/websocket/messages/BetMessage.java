package com.mgreau.wildfly.websocket.messages;

/**
 * This message can be send by both peers (client/server) :
 * <ul>
 * <li>by peer client : when a user bet on the winner</li>
 * <li>by peer server : 
 * 	<ul>
 * 		<li>each time another user bet on the winner (nbBets++)</li>
 * 		<li>each time another bet user closed his connection (nbBets--)</li>
 * 		<li>at the end of the match, to send the result </li>
 * 	</ul>
 * </li>
 * </ul>
 * 
 * @author contact@mgreau.com
 *
 */
public class BetMessage extends Message {
	
	/** Bet on this player */
	private String winner;
	
	/** OK / KO */
	private String result;
	
	/** Number of bets for the match */
	private Integer nbBets;
	
	public BetMessage(String winner){
		this.winner = winner;
		this.result = "";
		this.nbBets = 0;
	}
	
	public String getWinner(){
		return winner;
	}
    
	public String toString(){
		return "[BetMessage][nbBets]" + nbBets + " - BetWinner: ..." + winner;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public Integer getNbBets() {
		return nbBets;
	}


	public void setNbBets(Integer nbBets) {
		this.nbBets = nbBets;
	}
	
}
