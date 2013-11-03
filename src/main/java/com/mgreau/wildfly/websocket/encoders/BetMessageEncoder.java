package com.mgreau.wildfly.websocket.encoders;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.mgreau.wildfly.websocket.messages.BetMessage;

public class BetMessageEncoder implements Encoder.Text<BetMessage> {
	
	@Override
	public void init(EndpointConfig ec) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public String encode(BetMessage betMsg) throws EncodeException {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("winner",
					betMsg.getWinner()).add("nbBets", betMsg.getNbBets())
			.add("result", betMsg.getResult());
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
}
