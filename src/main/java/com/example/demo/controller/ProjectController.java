package com.example.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.controller.session.SessionData;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;

@Controller
public class ProjectController {

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	SessionData sessionData;

	@Autowired 
	ProjectValidator projectValidator;


	@RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectList = projectService.retrieveProjectsOwnedBy(loggedUser);
		model.addAttribute("user", loggedUser);
		model.addAttribute("projectList", projectList);
		return "MyOwnedProjects";
	}

	@RequestMapping(value = { "/projects/{projectId}" }, method = RequestMethod.GET) //L'URL che cattura la richiesta è parametrico: indichiamo il campo
	//parametrico con parentesi graffe
	public String project(Model model, @PathVariable Long projectId) {  //annotiamo con @PathVariable l'oggetto parametrico che ha lo stesso nome dato nell'URL
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		List<User> members = userService.getMembers(project);
		if (project == null)  //se il progetto non è presente nel DB
			return "redirect:/projects";


		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser))    //se l'utente loggato non risulta il proprietario e non è nella lista dei membri
			return "redirect:/projects";                                               //possiamo fare getOwner perché l'associazione con lo User è EAGER 

		model.addAttribute("project", project);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("members", members);

		return "project";
	}

	@RequestMapping(value = { "/projects/add" }, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectForm", new Project());
		return "addProject";
	}

	@RequestMapping(value = { "/projects/add" }, method = RequestMethod.POST)
	public String createProject(@Valid @ModelAttribute("projectForm") Project project, BindingResult projectBindingResult, Model model) {
		User loggedUser = sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if (!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
		this.projectService.saveProject(project);
		return "redirect:/projects/" + project.getId();
		}
		model.addAttribute("loggedUser", loggedUser);
		return "addProject";

	}
	
	@RequestMapping(value = {"/visibleProjects"}, method = RequestMethod.GET)
	public String myVisibleProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectList = projectService.retrieveProjectsSharedWith(loggedUser);
		model.addAttribute("user", loggedUser);
		model.addAttribute("projectList", projectList);
		return "MyVisibleProjects";
	}
}
