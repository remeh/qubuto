package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Project;
import models.User;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.ErrorCode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;

public class BaseController extends Controller {
	public static String renderNoErrorsJson() {
		return renderJson(ErrorCode.OK.getErrorCode(), ErrorCode.OK.getDefaultMessage());
	}
	
	public static String renderNotAuthenticatedJson() {
		return renderJson(ErrorCode.NOT_AUTHENTICATED.getErrorCode(), ErrorCode.NOT_AUTHENTICATED.getDefaultMessage());
	}
	
	public static String renderJson(int error, String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error", error);
		result.put("message", message);
		return Json.toJson(result).toString();
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
		Project project = Projects.findProject(userId, projectCleanName);
		
		return project;
	}
}
