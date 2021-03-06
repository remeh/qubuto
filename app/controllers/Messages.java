package controllers;

import java.util.Date;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.socket.action.MessageActions;
import com.mehteor.util.ErrorCode;

import models.Conversation;
import models.Message;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

public class Messages extends SessionController {
	public static Result add(String username, String projectCleanName, String conversationName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
		
		/*
		 * Find the corresponding conversation.
		 */
		Conversation conversation = Conversations.findConversation(username, projectCleanName, conversationName);
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		
		if (content == null || conversation == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally creates the message.
		 */
		
		Message message = new Message();
		message.setContent(Html.apply(content).toString());
		message.setAuthor(getUser());
		message.setLastUpdate(new Date());
		message.setConversation(conversation);
		
        /*
         * Retrieves the higher position.
         */
		ModelUtils<Message> muMessages = new ModelUtils<Message>(Message.class);
        Message lMessage = muMessages.last("position", "{conversation: #}", conversation.getId());
        long lastPosition = 0;
        if (lMessage != null) {
            lastPosition = lMessage.getPosition();
        }
        lastPosition++;
		message.setPosition(lastPosition);
		message.save();

		/*
		 * Broadcast the action
		 */
		MessageActions.newMessageAction(conversation.getId(), getUser(), message);

		return ok(BaseController.renderNoErrorsJson());
	}

	public static Result delete(String username, String projectCleanName, String conversationName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
		
		/*
		 * Find the corresponding conversation.
		 */
		Conversation conversation = Conversations.findConversation(username, projectCleanName, conversationName);
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String messageId = form.get("messageId");
		
		if (conversation == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Retrieves the message
		 */

		ModelUtils<Message> muMessages = new ModelUtils<Message>(Message.class);
		Message message = muMessages.find(messageId);

		if (message == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally deletes the message.
		 */
		
		message.remove();
		
		/*
		 * Broadcast the action
		 */
		
		MessageActions.deleteMessageAction(conversation.getId(), getUser(), message);

		return ok(BaseController.renderNoErrorsJson());
    }

	public static Result update(String username, String projectCleanName, String conversationName, String messageId) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
		
		/*
		 * Find the corresponding conversation.
		 */
		Conversation conversation = Conversations.findConversation(username, projectCleanName, conversationName);
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		
		if (content == null || conversation == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Retrieves the message
		 */
		ModelUtils<Message> muMessages = new ModelUtils<Message>(Message.class);
		Message message = muMessages.find(messageId);
		
		if (message == null) {
			return badRequest(renderJson(ErrorCode.BAD_PARAMETERS.getErrorCode(), ErrorCode.BAD_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally updates the message.
		 */
		
		message.setContent(Html.apply(content).toString());
		message.setLastUpdate(new Date());
		message.save();
		
		/*
		 * Broadcast the action
		 */
		
		MessageActions.updateMessageAction(conversation.getId(), getUser(), message);

		return ok(BaseController.renderNoErrorsJson());
	}
}
