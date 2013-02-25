package controllers;


import com.mehteor.qubuto.socket.manager.ConversationSubscriptionManager;

import play.api.templates.Html;
import play.mvc.*;
import scala.util.Random;

public class Application extends SessionController{
    public static Result index() {
    	// if the user is already authenticated
    	// redirect it to its dashboard page
    	if (isAuthenticated()) {
    		redirect(routes.Users.login());
    	}
    	
        return ok(views.html.index.render());
    }
    
    // ---------------------
    
	public static Html renderNotFound() {
		Random random = new Random();
    	int x = 320 + random.nextInt(40);
    	int y = 220 + random.nextInt(40);
    	return views.html.notFound.render(x,y);
	}
	
	public static Result status() {
		return ok();
	}
	
	public static Result stats() {
		StringBuilder builder = new StringBuilder();
		builder.append(Application.printQubuto());
		builder.append("\n");
		builder.append("State: RUNNING\n");
		builder.append("\n");
		builder.append("Sessions opened: ").append("TODO\n");
		builder.append("\n");
		builder.append("Conversations subscribers: ").append(ConversationSubscriptionManager.getInstance().getSubscribersCount()).append("\n");
		builder.append("ConversationActions in pool: ").append(ConversationSubscriptionManager.getInstance().getPoolSize()).append("\n");
		return ok(builder.toString());
	}
	
	// ---------------------
	
	public final static String printQubuto() {
		return "" +
				"               █\n" +
				"  ▓██▓         █               █          \n" +
				" ▒█  █▒        █               █          \n" +
				" █░  ░█ █   █  █▓██   █   █  █████   ███\n" +
				" █    █ █   █  █▓ ▓█  █   █    █    █▓ ▓█\n" +
				" █    █ █   █  █   █  █   █    █    █   █\n" +
				" █    █ █   █  █   █  █   █    █    █   █\n" +
				" █░  ░█ █   █  █   █  █   █    █    █   █\n" +
				" ▒█  █▓ █▒ ▓█  █▓ ▓█  █▒ ▓█    █░   █▓ ▓█\n" +
				"  ▓███  ▒██▒█  █▓██   ▒██▒█    ▒██   ███\n" +
				"    ▒█░\n" +
				"     ▓▒\n" +                   
				"\n";

	}
}
