package com.mehteor.qubuto.socket.action.todolist;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import controllers.Application;

import models.Task;
import models.User;

/**
 * Action created when someone creates a Task in a Todolist.
 * @author Rémy 'remeh' Mathieu
 */
public class NewTaskAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public NewTaskAction(User author, Task task) {
		super(author);
		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "NewTask");
		objectNode.putAll(author.toJsonPublic());
        objectNode.putAll(task.toJsonAction());
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
