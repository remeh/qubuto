package com.mehteor.qubuto.socket.action;

import com.mehteor.qubuto.socket.action.comment.AddCommentAction;
import com.mehteor.qubuto.socket.action.comment.DeleteCommentAction;
import com.mehteor.qubuto.socket.manager.TodolistSubscriptionManager;

import models.Comment;
import models.Task;
import models.User;

/**
 * Helpers to create actions for Comment
 * @author Rémy 'remeh' Mathieu
 */
public class CommentActions {
	public static void addCommentAction(String channelId, User author, Comment comment) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new AddCommentAction(author, comment));
		consumeActions(channelId);
	}
    
	public static void deleteCommentAction(String channelId, User author, Comment comment) {
		TodolistSubscriptionManager.getInstance().addAction(channelId, new DeleteCommentAction(author, comment));
		consumeActions(channelId);
	}

	// ---------------------
	
	public static void consumeActions(String channelId) {
		TodolistSubscriptionManager.getInstance().consumeActions(channelId);
	}
}

