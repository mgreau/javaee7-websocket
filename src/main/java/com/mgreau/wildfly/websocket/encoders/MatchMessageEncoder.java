package com.mgreau.wildfly.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wildfly.websocket.messages.MatchMessage;

/* Encode an MatchMessage as JSON.
 * For example, (new MatchMessage(tennisMatch))
 * is encoded as follows:
 {"bet":"Player x wins!","match":{"comments":"Welcome to this match between N. DJOKOVIC and R. NADAL.\n10:6:27 - Deuce\n10:6:27 - Deuce\n10:6:30 - Advantage N. DJOKOVIC\n10:6:30 - Advantage N. DJOKOVIC\n10:6:33 - Game N. DJOKOVIC\n10:6:51 - Game R. NADAL","serve":"player1","title":"US OPEN - FINAL","players":[{"name":"N. DJOKOVIC","games":1,"sets":0,"points":"15","set1":0,"set2":0,"set3":0},{"name":"R. NADAL","games":1,"sets":0,"points":"0","set1":0,"set2":0,"set3":0}]}} 
 */
public class MatchMessageEncoder implements Encoder.Text<MatchMessage> {
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(MatchMessage m) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add(
					"match",
					Json.createObjectBuilder()
							.add("serve", m.getMatch().getServe())
							.add("title", m.getMatch().getTitle())
							.add("players",
									Json.createArrayBuilder()
											.add(Json
													.createObjectBuilder()
													.add("name",
															m.getMatch()
																	.getPlayerOneName())
													.add("country",
															m.getMatch()
																	.getP1Country())
													.add("games",
															m.getMatch()
																	.getP1CurrentGame())
													.add("sets",
															m.getMatch()
																	.getP1Sets())
													.add("points",
															m.getMatch()
																	.getPlayer1Score())
													.add("set1",
															m.getMatch()
																	.getP1Set1())
													.add("set2",
															m.getMatch()
																	.getP1Set2())
													.add("set3",
															m.getMatch()
																	.getP1Set3()))
											.add(Json
													.createObjectBuilder()
													.add("name",
															m.getMatch()
																	.getPlayerTwoName())
													.add("games",
															m.getMatch()
																	.getP2CurrentGame())
													.add("country",
															m.getMatch()
																	.getP2Country())
													.add("sets",
															m.getMatch()
																	.getP2Sets())
													.add("points",
															m.getMatch()
																	.getPlayer2Score())
													.add("set1",
															m.getMatch()
																	.getP2Set1())
													.add("set2",
															m.getMatch()
																	.getP2Set2())
													.add("set3",
															m.getMatch()
																	.getP2Set3())))
							.add("comments", m.getMatch().getLiveComments())
							.add("finished", m.getMatch().isFinished())
							.add("nbBets", m.getNbBets())
							.add("betOn", m.getBetOn()==null?"":m.getBetOn()));

			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
