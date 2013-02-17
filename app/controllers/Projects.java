package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Project;
import models.User;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.session.SessionController;

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
			flash("error", "You don't have access to this user account.");
			return redirect(routes.Projects.list(user.getUsername()));
		}
	}
	
	public static Result test(String username, String project) {
		
		return ok(":)");
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
