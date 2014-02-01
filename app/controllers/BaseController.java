package controllers;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import models.Project;
import models.User;

import services.ProjectService;

import com.mehteor.db.ModelUtils;
import com.mehteor.util.ErrorCode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class BaseController extends Controller {
	public static String renderNoErrorsJson() {
		return renderJson(ErrorCode.OK.getErrorCode(), ErrorCode.OK.getDefaultMessage());
	}
	
	public static String renderNotAuthenticatedJson() {
		return renderJson(ErrorCode.NOT_AUTHENTICATED.getErrorCode(), ErrorCode.NOT_AUTHENTICATED.getDefaultMessage());
	}

    public static String renderNotAuthorizedJson() {
        return renderJson(ErrorCode.NOT_AUTHORIZED.getErrorCode(), ErrorCode.NOT_AUTHORIZED.getDefaultMessage());
    }
	
	public static String renderJson(int error, String message) {
		ObjectNode node = Json.newObject();
		node.put("error", error);
		node.put("message", message);
		return node.toString();
	}
	
	public static WebSocket<JsonNode> renderJsonSocket(final int error, final String message) {
		return new WebSocket<JsonNode>() {
			@Override
			public void onReady(play.mvc.WebSocket.In<JsonNode> in,
					play.mvc.WebSocket.Out<JsonNode> out) {
				ObjectNode jsonNode = Json.newObject();
				jsonNode.put("error", error);
				jsonNode.put("message", message);
				out.write(jsonNode);
				out.close();
			}
		};
	}
	
	// ---------------------
	
	protected static void setFlashError(String error) {
		if (error != null && !error.isEmpty()) {
			flash("error", error);
		}
	}
	
	/**
	 * Finds a user id by its username.
	 * @param username the username
	 * @return the user id found, or null
	 */
	protected static String getUserId(String username) {
		/*
		 * Retrieve the user's ID
		 */
		ModelUtils<User> muUsers = new ModelUtils<User>(User.class);
		List<User> users = muUsers.query("{'username': #}", username);
		if (users.size() > 1) {
			Logger.warn(String.format("Many users retrieved for the username [%s] !!", username));
			return users.get(0).getId();
		} else if (users.size() == 1) {
			return users.get(0).getId();
		}
		
		return null;
	}

	/**
	 * Finds a project by its owner and name.
	 * @param owner the owner of the project
	 * @param projectCleanName project clean name.
	 * @return the found project or null if none were found
	 */
	protected static Project findProject(String owner, String projectCleanName) {
		/*
		 * First, look for the user id.
		 */
		String userId = BaseController.getUserId(owner);
		if (userId == null) {
			return null;
		}

		/*
		 * Then, look for the project.
		 */
		Project project = ProjectService.findProject(userId, projectCleanName);
		
		return project;
	}
}
