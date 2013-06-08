package com.mehteor.util.session;

import play.Logger;

public class SessionCleanJob implements Runnable {
	@Override
	public void run() {
		//Logger.info(String.format("%d sessions cleaned.", SessionManager.cleanTimeoutedSessions()));
	}
}
