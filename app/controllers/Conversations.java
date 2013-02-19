package controllers;

import java.util.List;

import models.Conversation;
import models.Project;
import play.Logger;
import play.data.Form;
import play.mvc.Result;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.StringHelper;
import com.mehteor.qubuto.session.SessionController;

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
	        cleanTitle = StringHelper.generateNameId(form.field("title").value());
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
	
	public static Result show(String username, String projectname, String conversationname) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}

		String userId = SessionController.getUserId(username);
		if (userId == null) {
			return notFound(Application.renderNotFound());
		}
		
		Project project = Projects.findProject(userId, projectname);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}
		
		ModelUtils<Conversation> muConversations = new ModelUtils<Conversation>(Conversation.class);
		List<Conversation> conversations = muConversations.query("{'project': # , 'cleanTitle' : #}", project.getId(), conversationname);
		if (conversations.size() == 0) {
			return notFound(Application.renderNotFound());
		} else if (conversations.size() > 1) {
			Logger.warn(String.format("Many todolists for the projectname[%s], username[%s], cleanTitle[%s]", projectname, username, conversationname)); 
		}
		
		Conversation conversation = conversations.get(0);
		
		return ok(views.html.conversations.show.render(conversation));
	}
}
