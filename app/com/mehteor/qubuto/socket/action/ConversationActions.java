package com.mehteor.qubuto.socket.action;

import com.mehteor.qubuto.socket.action.conversation.TopicUpdateAction;
import com.mehteor.qubuto.socket.manager.ConversationSubscriptionManager;

import models.Conversation;
import models.User;

/**
 * Helpers to create actions.
 * @author Rémy 'remeh' Mathieu
 */
public class ConversationActions {
	public static void addTopicUpdateAction(String channelId, User author, Conversation conversation) {
		ConversationSubscriptionManager.getInstance().addAction(channelId, new TopicUpdateAction(author, conversation));
		consumeActions(channelId);
	}
	
	// ---------------------
	
	public static void consumeActions(String channelId) {
		ConversationSubscriptionManager.getInstance().consumeActions(channelId);
	}
}
