package com.mehteor.qubuto.socket.action.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.mehteor.qubuto.socket.action.Action;

import controllers.Application;

import models.Task;
import models.User;

/**
 * Action created when someone opens a Task in a Todolist.
 * @author Rémy 'remeh' Mathieu
 */
public class OpenTaskAction extends Action {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public OpenTaskAction(User author, Task task) {
		super(author);
		objectNode = Action.createNoErrorsNode();
		objectNode.put("action", "OpenTask");
        objectNode.put("date", Application.formater.format(new Date()));
		objectNode.putAll(author.toJsonPublic());
		objectNode.put("task", task.toJsonAction());
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}

