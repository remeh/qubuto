package com.mehteor.qubuto.socket.action;

import org.codehaus.jackson.JsonNode;

import models.User;

public abstract class Action {
	private boolean sendToAuthor;
	protected User author;

	// ---------------------

	public Action(User author) {
		this.author = author;
		this.sendToAuthor = false;
	}

	public Action(User author, boolean sendToAuthor) {
		this.author = author;
		this.sendToAuthor = sendToAuthor;
	}

	// ---------------------

	public abstract JsonNode toJson();
	
	@Override
	public String toString() {
		return toJson().toString();
	}

	public boolean isAuthor(User user) {
		return author.getId().equals(user.getId());
	}

	public boolean toSendToAuthor() {
		return sendToAuthor;
	}
}
