package controllers;

import models.Project;
import models.Todolist;
import play.Logger;
import play.data.Form;
import play.mvc.Result;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.session.SessionController;

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
        if (form.field("name").valueOr("").isEmpty()) {
        	form.reject("name", "Required");
        }
        if (form.field("description").valueOr("").isEmpty()) {
        	form.reject("description", "Required");
        }
        /*
         * Redirect to the create form if there is errors
         */
        if (form.hasErrors()) {
        	return badRequest(views.html.todolist.create.render(project, form));
        }
        
        /*
         * Saves the todolist
         */
        
        Todolist todolist = form.get();
        todolist.setProject(project);
        todolist.save();
		
		return ok("Todolist created.");
	}
}
