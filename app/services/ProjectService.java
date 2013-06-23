package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Project;
import models.User;
import models.UserRight;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.right.RightCategory;
import com.mehteor.qubuto.right.RightType;

import play.Logger;
import play.data.Form;
import play.mvc.Result;

public class ProjectService {
    /**
     * Finds the projects shared with the given user.
     * @param user      the user for which you want to find shared projects.
     * @return the projects shared with the user.
     */
    public static Set<Project> findSharedProjectsOfUser(User user) {
        if (user == null) {
            return new HashSet<Project>();
        }

        ModelUtils<UserRight> muRights = new ModelUtils<UserRight>(UserRight.class);
        List<UserRight> rights = muRights.query("{type: #, user: #, category: #}", RightType.READ, user.getId(), RightCategory.PROJECT);

        Set<Project> projects = new HashSet<Project>();
        for (UserRight right : rights) {
            projects.add(right.getProject());
        }

        return projects;
    }

	/**
	 * Finds the projects of the provided user. 
	 * @param user the user for which we want to find the projects
	 * @return the projects of this user.
	 */
	public static List<Project> findProjectsOfUser(User user) {
		if (user == null) {
			return new ArrayList<Project>();
		}
		
		ModelUtils<Project> muProject = new ModelUtils<Project>(Project.class);
		List<Project> projects = muProject.queryWithSort("{'creator': # }","{'creationDate':1}", user.getId());
		
		if (projects == null) {
			return new ArrayList<Project>();
		}
		
		return projects;
	}
      
	/**
	 * Finds a project with the owners' id and the project's clean name.
	 * @param userId the owners' id
	 * @param projectCleanName the project clean name
	 * @return the found project, null otherwise
	 */
	public static Project findProject(String userId, String projectCleanName) {
		ModelUtils<Project> muProjects = new ModelUtils<Project>(Project.class);
		List<Project> projects = muProjects.query("{'creator': #, 'cleanName': #}", userId, projectCleanName);
		
		if (projects.size() == 0) {
			// TODO project not found
			// TODO want to create a project ?
			return null;
		} else if (projects.size() > 1) {
			// This should never happened !
			// This user has many projects with the same name.
			Logger.warn(String.format("The user[%s] has many projects called \"%s\" !", userId, projectCleanName));
		}	
		
		/*
		 * Gets the project
		 */
		Project project = projects.get(0);
		if (project == null) {
			// should never happen but heh..
			Logger.warn(String.format("A null project has been extracted from the database for the user[%s], projectCleanName[%s]", userId, projectCleanName));
		}
		
		return project;
	}
}
