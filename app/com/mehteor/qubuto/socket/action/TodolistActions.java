package com.mehteor.qubuto.socket.action;

import com.mehteor.qubuto.socket.action.todolist.NewCommentAction;
import com.mehteor.qubuto.socket.action.todolist.DeleteCommentAction;
import com.mehteor.qubuto.socket.action.todolist.CloseTaskAction;
import com.mehteor.qubuto.socket.action.todolist.OpenTaskAction;
import com.mehteor.qubuto.socket.action.todolist.NewTaskAction;
import com.mehteor.qubuto.socket.action.todolist.DeleteTaskAction;
import com.mehteor.qubuto.socket.action.todolist.AddTagAction;
import com.mehteor.qubuto.socket.action.todolist.RemoveTagAction;
import com.mehteor.qubuto.socket.manager.TodolistSubscriptionManager;

import models.Comment;
import models.Task;
import models.User;

/**
 * Helpers to create actions for Todolist
 * @author Rémy 'remeh' Mathieu
 */
public class TodolistActions {
	public static void closeTaskAction(String channelId, User author, Task task) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new CloseTaskAction(author, task));
		consumeActions(channelId);
	}

	public static void openTaskAction(String channelId, User author, Task task) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new OpenTaskAction(author, task));
		consumeActions(channelId);
	}

	public static void newTaskAction(String channelId, User author, Task task) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new NewTaskAction(author, task));
		consumeActions(channelId);
	}

	public static void deleteTaskAction(String channelId, User author, Task task) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new DeleteTaskAction(author, task));
		consumeActions(channelId);
	}

	public static void newCommentAction(String channelId, User author, Task task, Comment comment) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new NewCommentAction(author, task, comment));
		consumeActions(channelId);
	}

	public static void deleteCommentAction(String channelId, User author, Task task, Comment comment) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new DeleteCommentAction(author, task, comment));
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
