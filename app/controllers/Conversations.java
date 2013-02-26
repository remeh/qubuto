package controllers;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import models.Conversation;
import models.Project;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F.*;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.WebSocket;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ErrorCode;
import com.mehteor.qubuto.StringHelper;
import com.mehteor.qubuto.socket.Subscriber;
import com.mehteor.qubuto.socket.action.ConversationActions;
import com.mehteor.qubuto.socket.manager.ConversationSubscriptionManager;

public class Conversations extends SessionController {
	public static Form<Conversation> conversationForm = Form.form(Conversation.class);

	/**
	 * Creates a conversation.
	 * @return Result
	 */
	public static Result submit() {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		Form<Conversation> form = conversationForm.bindFromRequest();

		/*
		 * Look for the attached project
		 */
		
		if (form.field("conversation_project_id").valueOr("").isEmpty()) {
			flash("error", "An error occurred with project.");
			Logger.warn(String.format("The user[%s] tried to create a conversation for a project without its id.", getUser().getId()));
		}
		
		String projectId = form.field("conversation_project_id").value();
		ModelUtils<Project> muProjects = new ModelUtils<Project>(Project.class);
		Project project = muProjects.find(projectId);
		
		if (project == null) {
			flash("error", "An error occurred with project.");
			Logger.warn(String.format("The user[%s] tried to create a conversation for a project without its id.", getUser().getId()));
		}
		
        /*
         *  Other required fields
         */
		String cleanTitle = null;
        if (form.field("title").valueOr("").isEmpty()) {
        	form.reject("title", "Required");
        } else {
	        /*
	         * You can't have more than one todolist of this name.
	         */
	        ModelUtils<Conversation> muConversations = new ModelUtils<Conversation>(Conversation.class);
	        cleanTitle = StringHelper.cleanString(form.field("title").value(), "-");
	        List<Conversation> foundExisting = muConversations.query("{'project': #, 'cleanTitle': # }", project.getId(), cleanTitle);
	        if (foundExisting.size() > 0) {
	        	form.reject("title", "This name is conflicting with another conversation in this project.");
	        }
        }
        
	    /*
	     * Redirect to the create form if there is errors
	     */
	    if (form.hasErrors()) {
	    	return badRequest(views.html.conversations.create.render(project, form));
	    }
        
        /*
         * Saves the conversation
         */
        
        Conversation conversation = form.get();
        conversation.setProject(project);
        conversation.setCleanTitle(cleanTitle);
        conversation.save();
		
        return redirect(routes.Conversations.show(getUser().getUsername(), project.getName(), cleanTitle));
	}
	
	public static Result show(String username, String projectName, String conversationName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		Conversation conversation = Conversations.findConversation(username, projectName, conversationName);
		if (conversation == null) {
			return badRequest(Application.renderNotFound());
		}
		
		String content = "";
		if (conversation.getContent() != null) {
			content = Json.stringify(Json.toJson(conversation.getContent()));
		}
		
		/*
		 * Generate the websocket URI.
		 */
		
		String websocketUri = String.format("ws://%s%s", request().host(), routes.Conversations.subscribe(username, projectName, conversationName).url());
		
		return ok(views.html.conversations.show.render(conversation, websocketUri, content));
	}
	
	
	/*
	 * AJAX part
	 */
	
	public static Result update(String username, String projectName, String conversationName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		if (content == null) {
			return badRequest(renderJson(ErrorCode.NOT_ENOUGH_PARAMETERS.getErrorCode(), ErrorCode.NOT_ENOUGH_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Remove html tags! Normally already done by Pagedown
		 * but to avoid injection or something else. 
		 */
		
		content = content.replaceAll("\\<.*?>","");
		
		Conversation conversation = findConversation(username, projectName, conversationName);
		conversation.setContent(content);
		conversation.save();
		
		/*
		 * Broadcast the action
		 */
		
		ConversationActions.addTopicUpdateAction(conversation.getId(), getUser(), content);
		
		return ok(BaseController.renderNoErrorsJson());
	}
	
//	public static Result getContent(String username, String projectName, String conversationName) {
//		if (!isAuthenticated("You're not authenticated.", true)) {
//			return badRequest(BaseController.renderNotAuthenticatedJson());
//		}
//		
//		// Get the conversation
//		Conversation conversation = findConversation(username, projectName, conversationName);
//		// Get the content
//		String content = conversation.getContent();
//		// If null, set to empty
//		if (content == null) {
//			content = "";
//		}
//		// Preserve for Javascript.
//		content.replace("\"", "\\\"");
//		// Build the JSON response.
//		Map<String, Object> hashMap = new HashMap<String, Object>();
//		hashMap.put("content", conversation.getContent());
//		hashMap.put("error", 0);
//		return ok(Json.toJson(hashMap).toString());
//	}
	
	/**
	 * Opens a websocket on the provided conversation.
	 */
	public static WebSocket<JsonNode> subscribe(final String username, final String projectName, final String conversationName) {
//		if (!isAuthenticated()) {
//			return BaseController.renderJsonSocket(ErrorCode.NOT_AUTHENTICATED.getErrorCode(), ErrorCode.NOT_AUTHENTICATED.getDefaultMessage());
//		}
//		
//		final Conversation conversation = Conversations.findConversation(username, projectName, conversationName);
//		if (conversation == null) {
//			return BaseController.renderJsonSocket(ErrorCode.NOT_ENOUGH_PARAMETERS.getErrorCode(), ErrorCode.NOT_ENOUGH_PARAMETERS.getDefaultMessage());
//		}
		
		final User user = getUser();
		final Conversation conversation = findConversation(username, projectName, conversationName);
		
		if (conversation == null) {
			return null;
		}
		
		// Opens the websocket
		return new WebSocket<JsonNode>() {
			public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
				final Subscriber subscriber = new Subscriber(out, user);
				
				// For each event received on the socket,
				in.onMessage(new Callback<JsonNode>() {
					public void invoke(JsonNode event) {
						// TODO nuffin to do ?
					}
				});
				
				// When the socket is closed, removes the subscription.
				in.onClose(new Callback0() {
					public void invoke() {
						ConversationSubscriptionManager.getInstance()
									.unsubscribe(conversation.getId(), subscriber);
					}
				});
				
				// Subscribe the socket to the conversation.
				ConversationSubscriptionManager.getInstance()
							.subscribe(conversation.getId(), subscriber);
			}
			
		};
		
	}
	
	// ---------------------
	
	private static Conversation findConversation(String username, String projectName, String conversationName) {
		Project project = findProject(username, projectName);
		
		if (project == null) {
			return null;
		}
		
		ModelUtils<Conversation> muConversations = new ModelUtils<Conversation>(Conversation.class);
		List<Conversation> conversations = muConversations.query("{'project': # , 'cleanTitle' : #}", project.getId(), conversationName);
		if (conversations.size() == 0) {
			return null;
		} else if (conversations.size() > 1) {
			Logger.warn(String.format("Many todolists for the projectname[%s], username[%s], cleanTitle[%s]", projectName, username, conversationName)); 
		}
		
		return conversations.get(0);
	}
}
