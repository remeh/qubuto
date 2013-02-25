package com.mehteor.qubuto.socket.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;

import com.mehteor.qubuto.socket.Subscriber;
import com.mehteor.qubuto.socket.action.Action;

/**
 * Manages the connection on collaborative features of Qubuto.
 */
public class SubscriptionManager {
	private static Map<String, List<Subscriber>> channelsSubscribers = new HashMap<String, List<Subscriber>>();
	private static Map<String, List<Action>> actionsQueues = new HashMap<String, List<Action>>();

	/**
	 * Singleton instance.
	 */
	private static SubscriptionManager instance;

	private static int subscribersCount = 0;

	// ---------------------

	/**
	 * Singleton constructor.
	 */
	protected SubscriptionManager() {
	}

	// ---------------------

	/**
	 * Subscribes to a channel.
	 * @param id the channel id (could be a conversation id, a todolist id, ...)
	 * @param subscriber
	 */
	public void subscribe(String id, Subscriber subscriber) {
		if (id == null || id.isEmpty() || subscriber == null) {
			return;
		}

		synchronized (channelsSubscribers) {
			List<Subscriber> projectSubscribers = channelsSubscribers
					.get(id);

			if (projectSubscribers == null) {
				projectSubscribers = new ArrayList<Subscriber>();
				channelsSubscribers.put(id, projectSubscribers);
				subscribersCount++;
				
			}

			projectSubscribers.add(subscriber);
		}
	}

	public void unsubscribe(String projectId, Subscriber subscriber) {
		if (projectId == null || projectId.isEmpty() || subscriber == null) {
			return;
		}

		synchronized (channelsSubscribers) {
			List<Subscriber> subscribers = channelsSubscribers.get(projectId);
			if (subscribers != null) {
				subscribers.remove(subscriber);
				subscribersCount--;
			}
		}
	}

	public void consumeActions(String channelId) {
		List<Action> actionsQueue = actionsQueues.get(channelId);
		if (actionsQueue != null) {
			
			List<Subscriber> channelSubscribers = channelsSubscribers.get(channelId);
			
			// if there is no more subscribers, forget the channel.
			if (channelSubscribers == null || channelSubscribers.size() == 0) {
				actionsQueues.remove(channelId);
				channelsSubscribers.remove(channelId);
				Logger.error(String.format("No more subscribers to channelId[%s]", channelId));
			} else {
				// send every actions queued for this channel to subscribers
				for (Action action : actionsQueue) {
					for (Subscriber subscriber : channelSubscribers) {
						// TODO
						// do not send the action to the author
						if (!action.isAuthor(subscriber.getUser()))
						{
							subscriber.sendAction(action);
						}
					}
					Logger.error(String.format("%s consumed by %d subscribers.", action.getClass().getSimpleName(), 0));
				}
			}
			
			actionsQueue.clear();
		}
	}

	public void addAction(String id, Action action) {
		if (id == null) {
			Logger.error(String.format("A null id has been provided when adding an action %s.", action.getClass().getSimpleName()));
			return;
		}
	
		if (action == null) {
			Logger.error(String.format("A null action has been provided the id[%s]", id));
			return;
		}

		synchronized (actionsQueues) {
			List<Action> actionsQueue = actionsQueues.get(id);
			if (actionsQueue == null) {
				actionsQueue = new ArrayList<Action>();
				actionsQueues.put(id, actionsQueue);
			}

			actionsQueue.add(action);
		}
	}

	// ---------------------

	public static SubscriptionManager getInstance() {
		if (instance == null) {
			instance = new SubscriptionManager();
		}
		return instance;
	}

	public int getSubscribersCount() {
		return subscribersCount;
	}
	
	public int getPoolSize() {
		int nbActions = 0;
		for (String actionQueue: actionsQueues.keySet()) {
			nbActions += actionsQueues.get(actionQueue).size();
		}
		return nbActions;
	}
}
