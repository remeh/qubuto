package com.mehteor.qubuto.ajax.action;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.util.ErrorCode;

import play.libs.Json;

import models.User;

/**
 * A simple ajax action done in Qubuto.
 *
 * @author Rémy 'remeh' Mathieu
 */
public abstract class AjaxAction {
	protected User author;

	// ---------------------

	/**
	 * Default constructor. This action won't be send to the author.
	 * @param author the author of the action.
	 */
	public AjaxAction(User author) {
		this.author = author;
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
}

