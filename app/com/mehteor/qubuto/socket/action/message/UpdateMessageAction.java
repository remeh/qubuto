package com.mehteor.qubuto.socket.action.message;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import controllers.Application;

import models.Message;
import models.User;

/**
 * Action created when someone updates a message in a Conversation.
 * @author Rémy 'remeh' Mathieu
 */
public class UpdateMessageAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public UpdateMessageAction(User author, Message message) {
		super(author);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Application.COMPLETE_DATE_PATTERN);
		
		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "UpdateMessage");
		objectNode.putAll(author.toJsonPublic());
		objectNode.put("id", message.getId());
		objectNode.put("position", message.getPosition());
		objectNode.put("content", message.getContent());
		objectNode.put("creationDate", simpleDateFormat.format(message.getCreationDate()));
		objectNode.put("lastUpdate", simpleDateFormat.format(message.getLastUpdate()));
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
