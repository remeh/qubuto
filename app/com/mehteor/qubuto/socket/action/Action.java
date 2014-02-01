package com.mehteor.qubuto.socket.action;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.util.ErrorCode;

import play.libs.Json;

import models.User;

/**
 * An action done in Qubuto, which should be broacasted
 * to some other users.
 * @author Rémy 'remeh' Mathieu
 */
public abstract class Action {
	private boolean sendToAuthor;
	
	protected User author;

	// ---------------------

	/**
	 * Default constructor. This action won't be send to the author.
	 * @param author the author of the action.
	 */
	public Action(User author) {
		this.author = author;
		this.sendToAuthor = false;
	}

	/**
	 * Constructor which specify the author and if we should send the action to the author. 
	 * @param author the author of the action.
	 * @param sendToAuthor whether we should send this action to the author.
	 */
	public Action(User author, boolean sendToAuthor) {
		this.author = author;
		this.sendToAuthor = sendToAuthor;
	}

	// ---------------------
	
	/**
	 * Creates a basic node with a "no error" message.
	 * @return the created node.
	 */
	protected static ObjectNode createNoErrorsNode() {
		ObjectNode node = Json.newObject();
		node.put("error", ErrorCode.OK.getErrorCode());
		node.put("message", ErrorCode.OK.getDefaultMessage());
		return node;
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
