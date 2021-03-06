package com.mehteor.qubuto.ajax.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import controllers.Application;

import models.Project;
import models.User;

/**
 * Used to render the action of removing a Collaborator.
 * @author Rémy 'remeh' Mathieu
 */
public class RemoveCollaboratorAction extends AjaxAction {
	/**
	 * The {@link ObjectNode} used to render the action.
	 */
	private final ObjectNode objectNode;
	
	public RemoveCollaboratorAction(User author, Project project, User collaborator) {
		super(author);
		objectNode = AjaxAction.createNoErrorsNode();
		objectNode.put("action", "RemoveCollaborator");
        objectNode.put("date", Application.formater.format(new Date()));
		objectNode.putAll(author.toJsonPublic());
		objectNode.put("project", project.toJsonAction());
        objectNode.put("collaborator", collaborator.toJsonPublic());
	}
	
	// ---------------------
	
	@Override
	public JsonNode toJson() {
		return objectNode;
	}
}
