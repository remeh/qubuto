package controllers;

import java.util.Date;
import java.util.List;
import java.util.HashSet;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.socket.action.TodolistActions;
import com.mehteor.util.ErrorCode;

import models.Task;
import models.TaskState;
import models.Todolist;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Results;

public class Tasks extends SessionController {
    /**
     * Adds a tag on a task.
     * 
     * AJAX call.
     *
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
	public static Result addTag(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
        
//      // TODO rights

    	/*
    	 * Find the corresponding todolist.
    	 */
    	Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
    	
        // Binds the request
        DynamicForm form = Form.form().bindFromRequest();

        /*
         * Required fields.
         */
        
    	String taskId = form.get("taskId");
    	String tag = form.get("tag");
    	
    	if (taskId == null || tag == null) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        ModelUtils<Task> muTask = new ModelUtils<Task>(Task.class);
        List<Task> tasks = muTask.query("{'_id': # }", taskId);

        if (tasks.size() == 0) {
            return Results.notFound(""); // 404
        } else if (tasks.size() >= 2) {
            // TODO log something here ?
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        Task task = tasks.get(0);
        task.getTags().add(tag);
        task.save();

		/*
		 * Broadcast the action
		 */
		TodolistActions.addTagAction(todolist.getId(), getUser(), task, tag);

		return ok(BaseController.renderNoErrorsJson());
    }

    /**
     * Removes a tag on a task.
     * 
     * AJAX call.
     *
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
	public static Result removeTag(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
        
//      // TODO rights

    	/*
    	 * Find the corresponding todolist.
    	 */
    	Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
    	
        // Binds the request
        DynamicForm form = Form.form().bindFromRequest();

        /*
         * Required fields.
         */
        
    	String taskId = form.get("taskId");
    	String tag = form.get("tag");
    	
    	if (taskId == null || tag == null) {
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        ModelUtils<Task> muTask = new ModelUtils<Task>(Task.class);
        List<Task> tasks = muTask.query("{'_id': # }", taskId);

        if (tasks.size() == 0) {
            return Results.notFound(""); // 404
        } else if (tasks.size() >= 2) {
            // TODO log something here ?
    		return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        Task task = tasks.get(0);
        task.getTags().remove(tag);
        task.save();

		/*
		 * Broadcast the action
		 */
		TodolistActions.removeTagAction(todolist.getId(), getUser(), task, tag);

		return ok(BaseController.renderNoErrorsJson());
    }

    /**
     * Ajax
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
	public static Result add(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
        
        // TODO rights
		
		/*
		 * Find the corresponding todolist.
		 */
		Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		String title = form.get("title");
		
		if (content == null || title == null || todolist == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally creates the task.
		 */
		Task task = new Task();
		task.setAuthor(getUser());
		task.setContent(content);
		task.setTitle(title);
		task.setCreationDate(new Date());
		task.setTodolist(todolist);
		task.setState(TaskState.TODO);
		task.setTags(new HashSet<String>());
		
		/*
		 * Set its position value.
		 */
		ModelUtils<Task> muTask = new ModelUtils<Task>(Task.class);
		long count = muTask.count("{todolist: #}", todolist.getId());
		task.setPosition(count); // FIXME TODO this position is later used in the view, but it could be shared between
								 // FIXME TODO many tasks if they're saved in the same time....
		task.save();
		
		/*
		 * Broadcast the action
		 */
		
		TodolistActions.newTaskAction(todolist.getId(), getUser(), task);

		return ok(BaseController.renderNoErrorsJson());
	}

    /**
     * Ajax
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
	public static Result delete(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
        
        // TODO rights
		
		/*
		 * Find the corresponding todolist.
		 */
		Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String taskId = form.get("taskId");
		
		if (taskId == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Looks for the task.
		 */
		ModelUtils<Task> muTask = new ModelUtils<Task>(Task.class);
		Task task = muTask.find(taskId);
        
        if (task == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}

        System.out.println(task);

		/*
		 * Finally deletes the task.
		 */
		task.remove();
		
		/*
		 * Broadcast the action
		 */
		
		TodolistActions.deleteTaskAction(todolist.getId(), getUser(), task);

		return ok(BaseController.renderNoErrorsJson());
	}
}
