package com.mehteor.qubuto.socket.action.todolist;

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
 * Action created when someone creates a Comment in a Task.
 * @author Rémy 'remeh' Mathieu
 */
public class NewCommentAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public NewCommentAction(User author, Task task, Comment comment) {
		super(author);
		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "NewComment");
        objectNode.put("date", Application.formater.format(new Date()));
		objectNode.putAll(author.toJsonPublic());
        objectNode.put("task", task.toJsonAction());
        objectNode.put("comment", comment.toJsonAction());
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
