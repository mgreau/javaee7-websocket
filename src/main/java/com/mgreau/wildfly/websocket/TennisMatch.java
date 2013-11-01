package com.mgreau.wildfly.websocket;

import java.util.Calendar;
import java.util.logging.Logger;

public class TennisMatch {
	
	private static final Logger LOG = Logger.getLogger("TennisMatch");

	private String key;
	private String title;
	
	private String p2Name;
	private String p1Name;

	private int p1Points = 0;
	private int p2Points = 0;
	private int p1Sets = 0;
	private int p2Sets = 0;
	private int p1Set1 = 0;
	private int p1Set2 = 0;
	private int p1Set3 = 0;
	private int p2Set1 = 0;
	private int p2Set2 = 0;
	private int p2Set3 = 0;

	private int p1GamesInCurrentSet = 0;
	private int p2GamesInCurrentSet = 0;

	private boolean isSet1Finished = false;
	private boolean isSet2Finished = false;
	private boolean isSet3Finished = false;
	
	private String serve;
	
	private StringBuffer liveComments = new StringBuffer() ;

	public String getPlayerOneName() {
		return p1Name;
	}

	public String getPlayerTwoName() {
		return p2Name;
	}

	public TennisMatch(String key, String title, String playerOneName, String playerTwoName) {
		this.key = key;
		this.title = title;
		this.p1Name = playerOneName;
		this.p2Name = playerTwoName;
		this.serve = p1Name;
		liveComments.append("Welcome to this match between " + p1Name + " and " + p2Name + ".");
		LOG.info("Match started : " + title + " (" + p1Name + "-" + p2Name + ")");
	}

	public String getKey() {
		return key;
	}

	/**
	 * Reset the match
	 */
	public synchronized void reset() {
		p1Points = p2Points = 0;
		p1Sets = p2Sets = 0;
		p1Set1 = p1Set2 = p1Set3 =  0;
		p2Set1 = p2Set2 = p2Set3 = 0;
		p1GamesInCurrentSet = p2GamesInCurrentSet = 0;
		isSet1Finished = isSet2Finished = isSet3Finished = false;
		liveComments = new StringBuffer();
		liveComments.append("Welcome to this match between " + p1Name + " and " + p2Name + ".");
	}

	public String getPlayer1Score() {
		if (hasAdvantage() && p1Points > p2Points) {
			addLiveComments("Advantage " + playerWithHighestScore());
			return "AD";
		}
		if (isDeuce()){
			addLiveComments("Deuce");
			return "40";
		}
		return translateScore(p1Points);
	}
	
	public String getPlayer2Score() {
		if (hasAdvantage() && p2Points > p1Points) {
			addLiveComments("Advantage " + playerWithHighestScore());
			return "AD";
		}
		if (isDeuce()){
			return "40";
		}
		return translateScore(p2Points);
	}

	private boolean isDeuce() {
		return p1Points >= 3 && p2Points == p1Points;
	}

	private String playerWithHighestScore() {
		if (p1Points > p2Points) {
			return p1Name;
		} else {
			return p2Name;
		}
	}
	
	private String playerWithHighestGames() {
		if (p1GamesInCurrentSet > p2GamesInCurrentSet) {
			return p1Name;
		} else {
			return p2Name;
		}
	}
	
	public String playerWithHighestSets() {
		if (p1Sets > p2Sets) {
			return p1Name;
		} else {
			return p2Name;
		}
	}

	public boolean hasMatchWinner() {
		if (isSet1Finished && isSet2Finished && (isSet3Finished || p1Sets != p2Sets))
			return true;
		return false;
	}
	
	public boolean hasGameWinner() {
		boolean hasGameWinner = false;
		if (p2Points >= 4 && p2Points >= p1Points + 2) {
			p2GamesInCurrentSet++;
			hasGameWinner = true;
		}
		if (p1Points >= 4 && p1Points >= p2Points + 2) {
			p1GamesInCurrentSet++;
			hasGameWinner = true;
		}
		if (hasGameWinner){
			addLiveComments("Game " + playerWithHighestScore());
			p2Points = p1Points = 0;
			if (p1Name.equals(serve)){
				serve=p2Name;
			} else {
				serve=p1Name;
			}
		}
		return hasGameWinner;
	}

	public boolean hasSetWinner() {
		if ((p1GamesInCurrentSet >= 6
				&& (p1GamesInCurrentSet >= p2GamesInCurrentSet + 2 || p1GamesInCurrentSet
						+ p2GamesInCurrentSet == 13))
			||	(p2GamesInCurrentSet >= 6
				&& (p2GamesInCurrentSet >= p1GamesInCurrentSet + 2 || p1GamesInCurrentSet
						+ p2GamesInCurrentSet == 13))		
				) {
			if (!isSet1Finished) {
				isSet1Finished = true;
				p1Set1 = p1GamesInCurrentSet;
				p2Set1 = p2GamesInCurrentSet;
			} else if (!isSet2Finished) {
				isSet2Finished = true;
				p1Set2 = p1GamesInCurrentSet;
				p2Set2 = p2GamesInCurrentSet;
			} else {
				isSet3Finished = true;
				p1Set3 = p1GamesInCurrentSet;
				p2Set3 = p2GamesInCurrentSet;
			}
			addLiveComments(playerWithHighestGames() + " wins this set !!");
			if (p1GamesInCurrentSet > p2GamesInCurrentSet)
				p1Sets++;
			else
				p2Sets++;
			p1GamesInCurrentSet = p2GamesInCurrentSet = 0;
			return true;
		}
		return false;
	}

	private boolean hasAdvantage() {
		if (p2Points >= 4 && p2Points == p1Points + 1)
			return true;
		if (p1Points >= 4 && p1Points == p2Points + 1)
			return true;

		return false;

	}

	public void playerOneScores() {
		p1Points++;
		if (hasGameWinner())
			hasSetWinner();
	}

	public void playerTwoScores() {
		p2Points++;
		if (hasGameWinner())
			hasSetWinner();
	}

	private String translateScore(int score) {
		switch (score) {
		case 3:
			return "40";
		case 2:
			return "30";
		case 1:
			return "15";
		case 0:
			return "0";
		}
		return "-";
	}

	public int getP1Points() {
		return p1Points;
	}

	public int getP2Points() {
		return p2Points;
	}

	public String getP2Name() {
		return p2Name;
	}

	public String getP1Name() {
		return p1Name;
	}

	public int getP1Set1() {
		return p1Set1;
	}

	public int getP1Set2() {
		return p1Set2;
	}

	public int getP1Set3() {
		return p1Set3;
	}

	public int getP2Set1() {
		return p2Set1;
	}

	public int getP2Set2() {
		return p2Set2;
	}

	public int getP2Set3() {
		return p2Set3;
	}

	public int getP1CurrentGame() {
		return p1GamesInCurrentSet;
	}

	public int getP2CurrentGame() {
		return p2GamesInCurrentSet;
	}

	public boolean isSet1Finished() {
		return isSet1Finished;
	}

	public boolean isSet2Finished() {
		return isSet2Finished;
	}

	public boolean isSet3Finished() {
		return isSet3Finished;
	}

	public String getLiveComments() {
		return liveComments.toString();
	}
	public void addLiveComments(String comments){
		  Calendar cal = Calendar.getInstance();
	        int H = cal.get(Calendar.HOUR);
	        int m = cal.get(Calendar.MINUTE);
	        int s = cal.get(Calendar.SECOND);
		liveComments.append("\n").append(H+":"+m+":"+s).append(" - ").append(comments);
		LOG.info(title + " (" + p1Name + "-" + p2Name + ") : " + comments);
	}

	public int getP1Sets() {
		return p1Sets;
	}

	public int getP2Sets() {
		return p2Sets;
	}

	public int getP1GamesInCurrentSet() {
		return p1GamesInCurrentSet;
	}

	public int getP2GamesInCurrentSet() {
		return p2GamesInCurrentSet;
	}

	public String getServe() {
		return serve;
	}

	public void setServe(String serve) {
		this.serve = serve;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}