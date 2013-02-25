package com.mehteor.qubuto.socket.action.conversation;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import models.User;

/**
 * Action created when someone changes the topic of a conversation
 * @author Rémy 'remeh' Mathieu
 */
public class TopicUpdateAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public TopicUpdateAction(User author, String content) {
		super(author);
		objectNode = Action.createNoErrorsNode();
		objectNode.put("content", content);
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
