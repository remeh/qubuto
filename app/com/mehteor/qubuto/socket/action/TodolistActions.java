package com.mehteor.qubuto.socket.action;

import com.mehteor.qubuto.socket.action.todolist.NewTaskAction;
import com.mehteor.qubuto.socket.action.todolist.AddTagAction;
import com.mehteor.qubuto.socket.action.todolist.RemoveTagAction;
import com.mehteor.qubuto.socket.manager.TodolistSubscriptionManager;

import models.Task;
import models.User;

/**
 * Helpers to create actions for Todolist
 * @author Rémy 'remeh' Mathieu
 */
public class TodolistActions {
	public static void newTaskAction(String channelId, User author, Task task) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new NewTaskAction(author, task));
		consumeActions(channelId);
	}

	public static void addTagAction(String channelId, User author, Task task, String tag) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new AddTagAction(author, task, tag));
		consumeActions(channelId);
	}

	public static void removeTagAction(String channelId, User author, Task task, String tag) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new RemoveTagAction(author, task, tag));
		consumeActions(channelId);
	}
	
	// ---------------------
	
	public static void consumeActions(String channelId) {
		TodolistSubscriptionManager.getInstance().consumeActions(channelId);
	}
}
