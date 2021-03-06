package controllers;

import java.util.Date;
import java.util.List;
import java.util.HashSet;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.socket.action.TodolistActions;
import com.mehteor.qubuto.right.RightCategory;
import com.mehteor.qubuto.right.RightType;
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
     * Closes a task.
     * 
     * AJAX call.
     *
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
	public static Result close(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
        
    	/*
    	 * Find the corresponding todolist.
    	 */
    	Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }

        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.TODOLIST, todolist, RightType.CLOSE_TASK, todolist.getProject());
        if (!right) {
            return SessionController.forbid(RightCategory.TODOLIST, RightType.CLOSE_TASK); 
        }
    	
        // Binds the request
        DynamicForm form = Form.form().bindFromRequest();

        /*
         * Required fields.
         */
        
    	String taskId = form.get("taskId");
    	
    	if (taskId == null) {
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
        task.setState(TaskState.DONE);
        task.save();

		/*
		 * Broadcast the action
		 */
		TodolistActions.closeTaskAction(todolist.getId(), getUser(), task);

		return ok(BaseController.renderNoErrorsJson());
    }

    /**
     * Opens a task.
     * 
     * AJAX call.
     *
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
	public static Result open(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}

    	/*
    	 * Find the corresponding todolist.
    	 */
    	Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
        
        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.TODOLIST, todolist, RightType.OPEN_TASK, todolist.getProject());
        if (!right) {
            return SessionController.forbid(RightCategory.TODOLIST, RightType.OPEN_TASK); 
        }
    	
        // Binds the request
        DynamicForm form = Form.form().bindFromRequest();

        /*
         * Required fields.
         */
        
    	String taskId = form.get("taskId");
    	
    	if (taskId == null) {
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
        task.setState(TaskState.TODO);
        task.save();

		/*
		 * Broadcast the action
		 */
		TodolistActions.openTaskAction(todolist.getId(), getUser(), task);

		return ok(BaseController.renderNoErrorsJson());
    }

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

        /*
         * Find the corresponding todolist.
         */
        Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
        
        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.TODOLIST, todolist, RightType.ADD_TAG, todolist.getProject());
        if (!right) {
            return SessionController.forbid(RightCategory.TODOLIST, RightType.ADD_TAG); 
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
        
    	/*
    	 * Find the corresponding todolist.
    	 */
    	Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
        
        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.TODOLIST, todolist, RightType.REMOVE_TAG, todolist.getProject());
        if (!right) {
            return SessionController.forbid(RightCategory.TODOLIST, RightType.REMOVE_TAG); 
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
		
		/*
		 * Find the corresponding todolist.
		 */
		Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
        
        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.TODOLIST, todolist, RightType.CREATE_TASK, todolist.getProject());
        if (!right) {
            return SessionController.forbid(RightCategory.TODOLIST, RightType.CREATE_TASK); 
        }
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		String title = form.get("title");
		
		if (title == null || todolist == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally creates the task.
		 */
		Task task = new Task();
		task.setAuthor(getUser());
		task.setContent(content);
        if (content == null) {
            task.setTitle(" ");
        }
		task.setTitle(title);
		task.setCreationDate(new Date());
		task.setTodolist(todolist);
		task.setState(TaskState.TODO);
		task.setTags(new HashSet<String>());
		

        /*
         * Retrieves the higher position.
         */
		ModelUtils<Task> muTasks = new ModelUtils<Task>(Task.class);
        Task lTask = muTasks.last("position", "{todolist: #}", todolist.getId());
        long lastPosition = 0;
        if (lTask != null) {
            lastPosition = lTask.getPosition();
        }
        lastPosition++;
		task.setPosition(lastPosition);
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
		
		/*
		 * Find the corresponding todolist.
		 */
		Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);

        if (todolist == null) {
            return Results.notFound(""); // 404
        }
        
        /*
         * Rights
         */

        boolean right = SessionController.hasRight(RightCategory.TODOLIST, todolist, RightType.DELETE_TASK, todolist.getProject());
        if (!right) {
            return SessionController.forbid(RightCategory.TODOLIST, RightType.DELETE_TASK); 
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
