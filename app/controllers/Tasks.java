package controllers;

import java.util.Date;
import java.util.HashSet;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ErrorCode;
import com.mehteor.qubuto.socket.action.TodolistActions;

import models.Message;
import models.Task;
import models.Todolist;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

public class Tasks extends SessionController {
	public static Result add(String username, String projectCleanName, String todolistName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
		
		/*
		 * Find the corresponding conversation.
		 */
		Todolist todolist = Todolists.findTodolist(username, projectCleanName, todolistName);
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		
		if (content == null || todolist == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally creates the task.
		 */
		Task task = new Task();
		task.setAuthor(getUser());
		task.setContent(content);
		task.setCreationDate(new Date());
		task.setTodolist(todolist);
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
}
