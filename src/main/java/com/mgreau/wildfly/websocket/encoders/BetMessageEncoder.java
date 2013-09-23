/**
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package com.mgreau.wildfly.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wildfly.websocket.messages.BetMessage;
import com.mgreau.wildfly.websocket.messages.MatchMessage;

/* Encode an MatchMessage as JSON.
 * For example, (new MatchMessage(tennisMatch))
 * is encoded as follows:
 {"bet":"Player x wins!","match":{"comments":"Welcome to this match between N. DJOKOVIC and R. NADAL.\n10:6:27 - Deuce\n10:6:27 - Deuce\n10:6:30 - Advantage N. DJOKOVIC\n10:6:30 - Advantage N. DJOKOVIC\n10:6:33 - Game N. DJOKOVIC\n10:6:51 - Game R. NADAL","serve":"player1","title":"US OPEN - FINAL","players":[{"name":"N. DJOKOVIC","games":1,"sets":0,"points":"15","set1":0,"set2":0,"set3":0},{"name":"R. NADAL","games":1,"sets":0,"points":"0","set1":0,"set2":0,"set3":0}]}} 
 */
public class BetMessageEncoder implements Encoder.Text<BetMessage> {
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(BetMessage m) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("winner",
					m.getWinner())
			.add("result", m.getResult());
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
