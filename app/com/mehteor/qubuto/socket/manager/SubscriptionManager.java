package com.mehteor.qubuto.socket.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mehteor.qubuto.socket.Subscriber;
import com.mehteor.qubuto.socket.action.Action;

import models.Project;

/**
 * Manages the connection on collaborative features of Qubuto.
 */
public class SubscriptionManager {
	private static Map<String, List<Subscriber>> projectsSubscribers = new HashMap<String, List<Subscriber>>();
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

	public void subscribe(String projectId, Subscriber subscriber) {
		if (projectId == null || projectId.isEmpty() || subscriber == null) {
			return;
		}

		synchronized (projectsSubscribers) {
			List<Subscriber> projectSubscribers = projectsSubscribers
					.get(projectId);

			if (projectSubscribers == null) {
				projectSubscribers = new ArrayList<Subscriber>();
				projectsSubscribers.put(projectId, projectSubscribers);
				subscribersCount++;
				
			}

			projectSubscribers.add(subscriber);
		}
	}

	public void unsubscribe(String projectId, Subscriber subscriber) {
		if (projectId == null || projectId.isEmpty() || subscriber == null) {
			return;
		}

		synchronized (projectsSubscribers) {
			List<Subscriber> subscribers = projectsSubscribers.get(projectId);
			if (subscribers != null) {
				subscribers.remove(subscriber);
				subscribersCount--;
			}
		}
	}

	public void consumeActions(String projectId) {
		List<Action> actionsQueue = actionsQueues.get(projectId);
		if (actionsQueue != null) {
			// should never happened because this tick is called by
			// the websocket.
			List<Subscriber> projectSubscribers = projectsSubscribers.get(projectId);
			if (projectSubscribers == null) {
				actionsQueue.clear();
			}

			// send every actions queued to subscribers
			for (Action action : actionsQueue) {
				for (Subscriber subscriber : projectSubscribers) {
					// TODO
					// do not send the action to the author
					if (!action.isAuthor(subscriber.getUser()))
					{
						subscriber.sendAction(action);
					}
				}
			}
			actionsQueue.clear();
		}
	}

	public void addAction(Project project, Action action) {
		if (project == null || action == null) {
			return;
		}

		synchronized (actionsQueues) {
			String projectId = project.getId();
			List<Action> actionsQueue = actionsQueues.get(projectId);
			if (actionsQueue == null) {
				actionsQueue = new ArrayList<Action>();
				actionsQueues.put(projectId, actionsQueue);
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
}
