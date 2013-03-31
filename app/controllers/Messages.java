package controllers;

import java.util.Date;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ErrorCode;
import com.mehteor.qubuto.socket.action.MessageActions;

import models.Conversation;
import models.Message;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

public class Messages extends SessionController {
	public static Result add(String username, String projectName, String conversationName) {
		if (!isAuthenticated("You're not authenticated.", true)) {
			return badRequest(BaseController.renderNotAuthenticatedJson());
		}
		
		/*
		 * Find the corresponding conversation.
		 */
		Conversation conversation = Conversations.findConversation(username, projectName, conversationName);
		
	    DynamicForm form = Form.form().bindFromRequest();

	    /*
	     * Required fields.
	     */
	    
		String content = form.get("content");
		
		if (content == null || conversation == null) {
			return badRequest(renderJson(ErrorCode.NOT_ENOUGH_PARAMETERS.getErrorCode(), ErrorCode.NOT_ENOUGH_PARAMETERS.getDefaultMessage()));
		}
		
		/*
		 * Finally creates the message.
		 */
		
		Message message = new Message();
		message.setContent(Html.apply(content).toString());
		message.setAuthor(getUser());
		message.setLastUpdate(new Date());
		message.setConversation(conversation);
		message.save();
		
		/*
		 * Set its position value.
		 */
		
		ModelUtils<Message> muMessages = new ModelUtils<Message>(Message.class);
		long count = muMessages.count("{conversation: #}", conversation.getId());
		message.setPosition(count-1);
		message.save();
		
		/*
		 * Broadcast the action
		 */
		
		MessageActions.newMessageAction(conversation.getId(), getUser(), message);

		return ok(BaseController.renderNoErrorsJson());
	}
}
