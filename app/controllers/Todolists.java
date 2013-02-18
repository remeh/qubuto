package controllers;

import java.util.List;

import models.Project;
import models.Todolist;
import play.Logger;
import play.data.Form;
import play.mvc.Result;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.session.SessionController;
import com.mehteor.qubuto.todolist.TodolistHelper;

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
        }
        if (form.field("description").valueOr("").isEmpty()) {
        	form.reject("description", "Required");
        } else {
	        
	        /*
	         * You can't have more than one todolist of this name.
	         */
	        ModelUtils<Todolist> muTodolists = new ModelUtils<Todolist>(Todolist.class);
	        cleanName = TodolistHelper.generateNameId(form.field("name").value());
	        List<Todolist> foundExisting = muTodolists.query("{'project': #, 'cleanName': # }", project.getId(), cleanName);
	        if (foundExisting.size() > 0) {
	        	form.reject("name", "This name is conflicting with another todolist in this project.");
	        }
	        
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
		
        return redirect(routes.Todolists.show(getUser().getUsername(), project.getName(), cleanName));
	}
	
	public static Result show(String username, String projectname, String todolistname) {
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
		
		ModelUtils<Todolist> muTodolists = new ModelUtils<Todolist>(Todolist.class);
		List<Todolist> todolists = muTodolists.query("{'project': # , 'cleanName' : #}", project.getId(), todolistname);
		if (todolists.size() == 0) {
			return notFound(Application.renderNotFound());
		} else if (todolists.size() > 1) {
			Logger.warn(String.format("Many todolists for the projectname[%s], username[%s], cleanName[%s]", projectname, username, todolistname)); 
		}
		
		Todolist todolist = todolists.get(0);
		
		return ok(todolist.getName());
	}
}
