package com.mehteor.qubuto.socket.action;

import com.mehteor.qubuto.socket.action.message.NewMessageAction;
import com.mehteor.qubuto.socket.action.message.UpdateMessageAction;
import com.mehteor.qubuto.socket.manager.ConversationSubscriptionManager;

import models.Message;
import models.User;

/**
 * Helpers to create actions.
 * @author Rémy 'remeh' Mathieu
 */
public class MessageActions {
	public static void newMessageAction(String channelId, User author, Message message) {
		ConversationSubscriptionManager.getInstance().addAction(channelId, new NewMessageAction(author, message));
		consumeActions(channelId);
	}
	
	public static void updateMessageAction(String channelId, User author, Message message) {
		ConversationSubscriptionManager.getInstance().addAction(channelId, new UpdateMessageAction(author, message));
		consumeActions(channelId);
	}
	
	public static void consumeActions(String channelId) {
		ConversationSubscriptionManager.getInstance().consumeActions(channelId);
	}
}
