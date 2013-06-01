package controllers;

import java.util.Date;
import java.util.List;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.socket.action.CommentActions;
import com.mehteor.util.ErrorCode;

import models.Task;
import models.Todolist;
import models.Comment;

import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Results;

public class Comments extends SessionController {
    /**
     * Adds a comment on a task.
     * 
     * AJAX call.
     *
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
    public static Result add(String username, String projectCleanName, String todolistName) {
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

        String taskId   = form.get("taskId");
        String content  = form.get("content");

        if (taskId == null || content == null) {
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

        Comment comment = new Comment();
        comment.setContent(content);
		comment.setAuthor(getUser());
        comment.setTask(task);
        comment.setCreationDate(new Date());
        comment.save();

		/*
		 * Broadcast the action
		 */
		CommentActions.addCommentAction(todolist.getId(), getUser(), comment);

		return ok(BaseController.renderNoErrorsJson());
    }
    
    /**
     * Removes a comment on a task.
     * 
     * AJAX call.
     *
     * @param username          the username of the owner of the project
     * @param projectCleanName  the project clean name
     * @param todolistName      the todolist clean name
     */
    public static Result remove(String username, String projectCleanName, String todolistName) {
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

        String taskId       = form.get("taskId");
        String commentId    = form.get("commentId");

        if (taskId == null || commentId == null) {
            return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        ModelUtils<Comment> muComment = new ModelUtils<Comment>(Comment.class);
        List<Comment> comments = muComment.query("{'_id': # }", commentId);

        if (comments.size() == 0) {
            return Results.notFound(""); // 404
        } else if (comments.size() >= 2) {
            // TODO log something here ?
            return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
        }

        Comment comment = comments.get(0);
        comment.remove();

		/*
		 * Broadcast the action
		 */
		CommentActions.removeCommentAction(todolist.getId(), getUser(), comment);

		return ok(BaseController.renderNoErrorsJson());
    }
}
