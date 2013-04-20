package controllers;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import models.Project;
import models.Todolist;
import models.User;
import play.Logger;
import play.data.Form;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Result;
import play.mvc.WebSocket;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ErrorCode;
import com.mehteor.qubuto.StringHelper;
import com.mehteor.qubuto.socket.Subscriber;
import com.mehteor.qubuto.socket.manager.ConversationSubscriptionManager;

public class Todolists extends SessionController {
	public static Form<Todolist> todolistForm = Form.form(Todolist.class);
	
	/**
	 * Creates a todolist.
	 * @return Result
	 */
	public static Result submit() {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		Form<Todolist> form = todolistForm.bindFromRequest();

		/*
		 * Look for the attached project
		 */
		
		if (form.field("todolist_project_id").valueOr("").isEmpty()) {
			flash("error", "An error occurred with project.");
			Logger.warn(String.format("The user[%s] tried to create a todolist for a project without its id.", getUser().getId()));
		}
		
		String projectId = form.field("todolist_project_id").value();
		ModelUtils<Project> muProjects = new ModelUtils<Project>(Project.class);
		Project project = muProjects.find(projectId);
		
		if (project == null) {
			flash("error", "An error occurred with project.");
			Logger.warn(String.format("The user[%s] tried to create a todolist for a project without its id.", getUser().getId()));
		}
		
        /*
         *  Other required fields
         */
		String cleanName = null;
        if (form.field("name").valueOr("").isEmpty()) {
        	form.reject("name", "Required");
        } else {
        	/*
        	 * You can't have more than one todolist of this name.
        	 */
        	ModelUtils<Todolist> muTodolists = new ModelUtils<Todolist>(Todolist.class);
        	cleanName = StringHelper.cleanString(form.field("name").value(), "-");
        	List<Todolist> foundExisting = muTodolists.query("{'project': #, 'cleanName': # }", project.getId(), cleanName);
        	if (foundExisting.size() > 0) {
        		form.reject("name", "This name is conflicting with another todolist in this project.");
        	}
        	
        }
        if (form.field("description").valueOr("").isEmpty()) {
        	form.reject("description", "Required");
        }
	        
        /*
         * Redirect to the create form if there is errors
         */
        if (form.hasErrors()) {
        	return badRequest(views.html.todolists.create.render(project, form));
        }
        
        /*
         * Saves the todolist
         */
        
        Todolist todolist = form.get();
        todolist.setProject(project);
        todolist.setCleanName(cleanName);
        todolist.save();
		
        return redirect(routes.Todolists.show(getUser().getUsername(), project.getCleanName(), cleanName));
	}
	
	public static Result show(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}

		Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);
		if (todolist == null ) {
			return notFound(Application.renderNotFound());
		}
		
		
		/*
		 * Generate the websocket URI.
		 */
		
		String websocketUri = String.format("ws://%s%s", request().host(), routes.Todolists.subscribe(username, projectCleanName, todolistName).url());
		
		
		return ok(views.html.todolists.show.render(todolist, websocketUri));
	}
	
	/**
	 * Opens a websocket on the provided conversation.
	 */
	public static WebSocket<JsonNode> subscribe(final String username, final String projectCleanName, final String todolistName) {
		if (!isAuthenticated()) {
			return BaseController.renderJsonSocket(ErrorCode.NOT_AUTHENTICATED.getErrorCode(), ErrorCode.NOT_AUTHENTICATED.getDefaultMessage());
		}
		
		final User user = getUser();
		final Todolist todolist = findTodolist(username, projectCleanName, todolistName);
		
		if (todolist == null) {
			return null; // TODO 404 ?
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
									.unsubscribe(todolist.getId(), subscriber);
					}
				});
				
				// Subscribe the socket to the conversation.
				ConversationSubscriptionManager.getInstance()
							.subscribe(todolist.getId(), subscriber);
			}
			
		};
	}
	
	
	// ---------------------
	
	private static Todolist findTodolist(String username, String projectName, String todolistName) {
		Project project = BaseController.findProject(username, projectName);
		
		if (project == null) {
			return null;
		}
		
		ModelUtils<Todolist> muTodolists = new ModelUtils<Todolist>(Todolist.class);
		List<Todolist> todolists = muTodolists.query("{'project': # , 'cleanName' : #}", project.getId(), todolistName);
		if (todolists.size() == 0) {
			return null;
		} else if (todolists.size() > 1) {
			Logger.warn(String.format("Many todolists for the projectname[%s], username[%s], cleanName[%s]", projectName, username, todolistName)); 
		}
		return todolists.get(0);
	}
}
