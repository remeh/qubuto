package com.mehteor.qubuto.socket;

import org.codehaus.jackson.JsonNode;

import com.mehteor.qubuto.socket.action.Action;

import models.User;
import play.mvc.WebSocket;

public class Subscriber {
	/**
	 * In channel of the socket.
	 */
//	private WebSocket.In<String> in;

	/**
	 * Out channel of the socket
	 */
	private WebSocket.Out<JsonNode> out;

	private User user;

	// ---------------------

	public Subscriber(/*WebSocket.In<String> in, */WebSocket.Out<JsonNode> out,
			User user) {
//		this.in = in;
		this.out = out;
		this.user = user;
	}

	// ---------------------

	public void close() {
		try {
			out.close();
		} catch (Exception e) {
			// OK
		}
	}

	public void sendAction(Action action) {
		out.write(action.toJson());
	}

	public User getUser() {
		return user;
	}
}
