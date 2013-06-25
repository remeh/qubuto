package controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Conversation;
import models.Project;
import models.Todolist;
import models.User;

import services.ProjectService;
import services.UserService;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ajax.AjaxConsumer;
import com.mehteor.qubuto.ajax.action.AddCollaboratorAction;
import com.mehteor.qubuto.ajax.action.RemoveCollaboratorAction;
import com.mehteor.qubuto.right.RightType;
import com.mehteor.qubuto.right.RightCategory;
import com.mehteor.util.StringHelper;
import com.mehteor.util.ErrorCode;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

public class Projects extends SessionController {
    final static Form<Project> projectForm = Form.form(Project.class);
    
    // ---------------------

	public static Result list(String username) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		
        User user = getUser();
		if (username.equals(user.getUsername())) {
			List<Project> userProjects = ProjectService.findProjectsOfUser(user);
            Set<Project> userSharedProjects = ProjectService.findSharedProjectsOfUser(user);
			return ok(views.html.projects.list.render(userProjects, userSharedProjects));
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
		Project project = ProjectService.findProject(userId, projectname);
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
        	
        /* Description isn't required.
        if (form.field("description").valueOr("").isEmpty()) {
        	form.reject("description", "Required.");
        }
        */

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
		Project project = ProjectService.findProject(userId, projectname);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}

        // TODO rights

        Set<String> collaborators = new HashSet<String>();
        for (User user : UserService.findCollaborators(project)) {
            collaborators.add(user.getUsername());
        }

		return ok(views.html.projects.settings.render(project, projectForm.fill(project), collaborators));
	}

    public static Result save(String username, String projectName, String action)
    {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		String userId = SessionController.getUserId(username);

		/*
		 * Look for the project 
		 */
		Project project = ProjectService.findProject(userId, projectName);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}
		
        if (action.equals("basics")) {
            return saveBasics(project);
        } 

        return badRequest();
    }

    // ---------------------- 
    // AJAX Calls
    
    public static Result removeCollaborator(String username, String projectName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		String userId = SessionController.getUserId(username);

		/*
		 * Look for the project 
		 */
		Project project = ProjectService.findProject(userId, projectName);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}

        // Binds the request
        DynamicForm form = Form.form().bindFromRequest();

        /*
         * Required fields.
         */
    	String collaboratorName = form.get("collaborator");

    	if (collaboratorName == null) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        ModelUtils<User> muUser = new ModelUtils<User>(User.class);
        User collaborator = muUser.findOne("username", collaboratorName);

        if (collaborator == null) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), "Unknown user."));
        }
        
        RemoveCollaboratorAction action = new RemoveCollaboratorAction(getUser(), project, collaborator);
        AjaxConsumer.consume(action);

        UserService.removeCollaborator(project, collaborator);

        return ok(action.toJson());
    }
    
    public static Result addCollaborator(String username, String projectName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return redirect(routes.Users.login());
		}
		String userId = SessionController.getUserId(username);

		/*
		 * Look for the project 
		 */
		Project project = ProjectService.findProject(userId, projectName);
		if (project == null) {
			return notFound(Application.renderNotFound());
		}

        // Binds the request
        DynamicForm form = Form.form().bindFromRequest();

        /*
         * Required fields.
         */
    	String collaboratorName = form.get("collaborator");

    	if (collaboratorName.equals(username)) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), "You can't add the creator as a collaborator"));
        }
    	
    	if (collaboratorName == null) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        ModelUtils<User> muUser = new ModelUtils<User>(User.class);
        User collaborator = muUser.findOne("username", collaboratorName);

        if (collaborator == null) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), "Unknown user."));
        }
        
        AddCollaboratorAction action = new AddCollaboratorAction(getUser(), project, collaborator);
        AjaxConsumer.consume(action);

        UserService.addCollaborator(project, collaborator);

        return ok(action.toJson());
    }

    // ---------------------- 
    
    /**
     * Saves the basics of the given project with the information provided in the projectForm.
     * @param project       the project to modify
     * @return Result of the request.
     */
    private static Result saveBasics(Project project) {
		if (project == null) {
            return notFound(Application.renderNotFound());
        }

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
        	return badRequest();
        }

        // TODO rights

        Project newProject = form.get();
        project.setName(newProject.getName());
        project.setCleanName(StringHelper.cleanString(form.field("name").value(), "-"));
        project.setDescription(newProject.getDescription());
        project.save();

		return redirect(routes.Projects.settings(project.getCreator().getUsername(), project.getCleanName()));
    }
}
