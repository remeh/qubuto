package com.mehteor.qubuto.socket.action.todolist;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import controllers.Application;

import models.Task;
import models.User;

/**
 * Action created when someone removes a Tag on a Task.
 * @author Rémy 'remeh' Mathieu
 */
public class RemoveTagAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public RemoveTagAction(User author, Task task, String tag) {
		super(author);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Application.COMPLETE_DATE_PATTERN);
		
		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "RemoveTask");
		objectNode.putAll(author.toJsonPublic());
		objectNode.put("taskId", task.getId());
		objectNode.put("tag", tag);
		// TODO
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
