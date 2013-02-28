package controllers;

import java.util.Date;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ErrorCode;

import models.Conversation;
import models.Message;
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
		
		Message message = new Message();
		message.setContent(content);
		message.setAuthor(getUser());
		message.setCreationDate(new Date());
		message.setConversation(conversation);
		message.save();
		
		ModelUtils<Message> muMessages = new ModelUtils<Message>(Message.class);
		long count = muMessages.count("{conversation: #}", conversation.getId());
		message.setPosition(count-1);
		message.save();
		
		return TODO;
	}
}
