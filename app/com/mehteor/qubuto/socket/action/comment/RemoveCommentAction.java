package com.mehteor.qubuto.socket.action.comment;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import controllers.Application;

import models.Comment;
import models.Task;
import models.User;

/**
 * Action created when someone removes a Comment on a Task.
 * @author Rémy 'remeh' Mathieu
 */
public class RemoveCommentAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public RemoveCommentAction(User author, Comment comment) {
		super(author);
		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "RemoveComment");
        objectNode.put("date", Application.formater.format(new Date()));
		objectNode.putAll(author.toJsonPublic());
		objectNode.put("comment", comment.toJsonAction());
		objectNode.put("taskId", comment.getTask().getId());
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}


