package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Project;
import models.Todolist;
import models.User;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.session.SessionController;

import play.Logger;
import play.data.Form;
import play.mvc.Result;

public class Projects extends SessionController {
    final static Form<Project> projectForm = Form.form(Project.class);
    
    // ---------------------

	public static Result list(String username) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		User user = SessionController.getUser();
		if (username.equals(user.getUsername())) {
			List<Project> userProjects = Projects.findProjectsOfUser(SessionController.getUser());
			return ok(views.html.projects.list.render(userProjects));
		} else {
			// TODO What to do when an user want to see another users page
			Logger.info(String.format("The user[%s] tried to access to the user \"%s\" page.", user.getId(), username));
			flash("error", "You don't have access to this user account.");
			return redirect(routes.Projects.list(user.getUsername()));
		}
	}
	
	public static Result show(String username, String projectname) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		/*
		 * Retrieve the user's ID
		 */
		User user = SessionController.getUser();
		String userId = user.getId();
		if (!username.equals(user.getUsername())) {
			// TODO find this user and retrieve its user id
			return TODO;
		}
		
		/*
		 * Look for the project 
		 */
		ModelUtils<Project> muProjects = new ModelUtils<Project>(Project.class);
		List<Project> projects = muProjects.query("{'creator': #, 'name': #}", userId, projectname);
		if (projects.size() == 0) {
			// TODO project not found
			return notFound(Application.renderNotFound());
		} else if (projects.size() > 1) {
			// This should never happened !
			// This user has many projects with the same name.
			Logger.warn(String.format("The user[%s] has many projects called \"%s\" !", userId, projectname));
		}
		
		/*
		 * Gets the project
		 */
		Project project = projects.get(0);
		if (project == null) {
			// should never happen but heh..
			Logger.warn(String.format("A null project has been extracted from the database for the user[%s], projectname[%s]", userId, projectname));
			return notFound(Application.renderNotFound());
		}

		return ok(views.html.projects.show.render(project, Form.form(Todolist.class)));
	}
	
	public static Result create() {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		return ok(views.html.projects.create.render(projectForm));
	}
	
	public static Result submit() {
		/*
         * Bind the form
         */
        Form<Project> form = projectForm.bindFromRequest(); 

        /*
         *  Required fields
         */
        if(form.field("name").valueOr("").isEmpty() || form.field("description").valueOr("").isEmpty()) {
        	return badRequest(views.html.projects.create.render(form));
        }

        User creator = SessionController.getUser();
        
        Project project = form.get();
        project.setCreator(creator);
		project.save();
        
		return redirect(routes.Projects.list(creator.getUsername()));
	}
	
	// ---------------------
	
	private static List<Project> findProjectsOfUser(User user) {
		if (user == null) {
			return new ArrayList<Project>();
		}
		
		ModelUtils<Project> muProject = new ModelUtils<Project>(Project.class);
		List<Project> projects = muProject.query("{'creator': # }", getUser().getId());
		
		if (projects == null) {
			return new ArrayList<Project>();
		}
		
		System.out.println(projects.size());
		
		return projects;
	}
}
