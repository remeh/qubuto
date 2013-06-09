package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Conversation;
import models.Project;
import models.Todolist;
import models.User;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.right.RightType;
import com.mehteor.qubuto.right.RightCategory;
import com.mehteor.util.StringHelper;

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
		
		String userId = SessionController.getUserId(username);
		
		/*
		 * Look for the project 
		 */
		Project project = findProject(userId, projectname);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}

        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.PROJECT, project, RightType.READ);
        if (!right) {
            return SessionController.forbid(RightCategory.PROJECT, RightType.READ); 
        }

		return ok(views.html.projects.show.render(project, Form.form(Todolist.class), Form.form(Conversation.class)));
	}
	
    /**
     * Page to create a Project.
     */
	public static Result create() {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		return ok(views.html.projects.create.render(projectForm));
	}
	
    /**
     * Creation of a Project.
     */
	public static Result submit() {
		/*
         * Bind the form
         */
        Form<Project> form = projectForm.bindFromRequest(); 

        /*
         *  Required fields
         */
        if (form.field("name").valueOr("").isEmpty()) {
        	form.reject("name", "Required.");
        } else if (StringHelper.validateString(form.field("name").value()) == false) {
    		form.reject("name", "Must be composed of letters, numbers or underscores.");
    	}
        	
        if (form.field("description").valueOr("").isEmpty()) {
        	form.reject("description", "Required.");
        }

        if (form.hasErrors()) {
        	return badRequest(views.html.projects.create.render(form));
        }
        
        User creator = SessionController.getUser();
        
        Project project = form.get();
        project.setCreator(creator);
        project.setCleanName(StringHelper.cleanString(form.field("name").value(), "-"));
		project.save();
        
		return redirect(routes.Projects.list(creator.getUsername()));
	}
	
    /**
     * Page to configure a Project.
     */
	public static Result settings(String username, String projectname) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
		String userId = SessionController.getUserId(username);
		
		/*
		 * Look for the project 
		 */
		Project project = findProject(userId, projectname);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}

        // TODO rights
		
		return ok(views.html.projects.settings.render(project));
	}
	
	// ---------------------
	
	/**
	 * Finds the projects of the provided user. 
	 * @param user the user for which we want to find the projects
	 * @return the projects of this user.
	 */
	private static List<Project> findProjectsOfUser(User user) {
		if (user == null) {
			return new ArrayList<Project>();
		}
		
		ModelUtils<Project> muProject = new ModelUtils<Project>(Project.class);
		List<Project> projects = muProject.query("{'creator': # }", getUser().getId());
		
		if (projects == null) {
			return new ArrayList<Project>();
		}
		
		return projects;
	}
	
	/**
	 * Finds a project with the owners' id and the project's clean name.
	 * @param userId the owners' id
	 * @param projectCleanName the project clean name
	 * @return the found project, null otherwise
	 */
	public static Project findProject(String userId, String projectCleanName) {
		ModelUtils<Project> muProjects = new ModelUtils<Project>(Project.class);
		List<Project> projects = muProjects.query("{'creator': #, 'cleanName': #}", userId, projectCleanName);
		
		if (projects.size() == 0) {
			// TODO project not found
			// TODO want to create a project ?
			return null;
		} else if (projects.size() > 1) {
			// This should never happened !
			// This user has many projects with the same name.
			Logger.warn(String.format("The user[%s] has many projects called \"%s\" !", userId, projectCleanName));
		}	
		
		/*
		 * Gets the project
		 */
		Project project = projects.get(0);
		if (project == null) {
			// should never happen but heh..
			Logger.warn(String.format("A null project has been extracted from the database for the user[%s], projectCleanName[%s]", userId, projectCleanName));
		}
		
		return project;
	}
}
