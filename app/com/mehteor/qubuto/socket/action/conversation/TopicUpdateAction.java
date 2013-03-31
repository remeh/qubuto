package com.mehteor.qubuto.socket.action.conversation;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import controllers.Application;

import models.Conversation;
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
	
	public TopicUpdateAction(User author, Conversation conversation) {
		super(author);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Application.COMPLETE_DATE_PATTERN);

		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "TopicUpdate");
		objectNode.put("lastUpdate", simpleDateFormat.format(conversation.getLastUpdate()));
		objectNode.put("content", conversation.getContent());
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
