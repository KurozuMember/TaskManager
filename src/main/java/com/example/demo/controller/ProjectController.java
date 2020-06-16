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
import com.example.demo.controller.validation.CredentialsValidator;
import com.example.demo.controller.validation.ProjectValidator;
import com.example.demo.model.Credentials;
import com.example.demo.model.Project;
import com.example.demo.model.User;

import com.example.demo.services.CredentialService;
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

	@Autowired
	CredentialService credentialsService;

	@Autowired
	CredentialsValidator credentialsValidator;


	@RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectList = projectService.retrieveProjectsOwnedBy(loggedUser);
		model.addAttribute("user", loggedUser);
		model.addAttribute("projectList", projectList);
		return "MyOwnedProjects";
	}

	//L'URL che cattura la richiesta è parametrico: indichiamo il campo parametrico con parentesi graffe
	@RequestMapping(value = { "/projects/{projectId}" }, method = RequestMethod.GET)
	public String project(Model model, @PathVariable Long projectId) { 	//annotiamo con @PathVariable l'oggetto parametrico che ha lo stesso nome dato nell'URL
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);

		//se il progetto non è presente nel DB
		if (project == null)  
			return "redirect:/projects";

		List<User> members = userService.getMembers(project);


		//se l'utente loggato non risulta il proprietario e non è nella lista dei membri
		if(!isProjectOwner(project, loggedUser) && !isProjectMember(project, loggedUser))		//possiamo fare getOwner perché l'associazione con lo User è EAGER
			return "redirect:/projects";              

		model.addAttribute("project", project);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("members", members);

		return "project";
	}

	@RequestMapping(value = { "/projects/add" }, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
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

	//il metodo cattura la richiesta della modifica del progetto e predispone una form di modifica
	@RequestMapping(value= { "/projects/update/{projectId}" }, method = RequestMethod.GET)
	public String projectForm(Model model, @PathVariable Long projectId) {
		Project project = projectService.getProject(projectId);
		User loggedUser = sessionData.getLoggedUser();
		if(!isProjectOwner(project, loggedUser)) {
			return "redirect:/projects/";
		}
		model.addAttribute("projectForm", project);
		return "projectUpdate";
	}

	@RequestMapping(value = { "/projects/update/{projectId}" }, method = RequestMethod.POST)
	public String updateProject(@Valid @ModelAttribute("projectForm") Project projectForm, @PathVariable Long projectId,
			BindingResult projectBindingResult, Model model) {
		projectValidator.validate(projectForm, projectBindingResult);
		if (!projectBindingResult.hasErrors()) {
			Project project = this.projectService.getProject(projectId);
			project.setDescription(projectForm.getDescription());
			project.setName(projectForm.getName());
			this.projectService.saveProject(project);
			return "projectUpdateSuccessful";
		}
		return "projectUpdate";
	}

	@RequestMapping(value = { "/projects/delete/{projectId}" }, method = RequestMethod.GET)
	public String confirmDeleteProject(Model model, @PathVariable Long projectId) {
		Project project = projectService.getProject(projectId);
		this.projectService.deleteProject(project);
		return "projectDelete";
	}

	@RequestMapping(value = {"/projects/share/{projectId}" }, method = RequestMethod.GET)
	public String shareProjectForm(Model model, @PathVariable Long projectId) {
		Project project = projectService.getProject(projectId);
		model.addAttribute("projectForm", project);
		model.addAttribute("credentialsForm", new Credentials());
		return "shareProject";
	}

	@RequestMapping(value = {"/projects/share/{projectId}"}, method = RequestMethod.POST)
	public String shareProject(Model model, @Valid @ModelAttribute ("credentialsForm") Credentials credentialsForm, BindingResult credentialsBindingResult, @PathVariable Long projectId) {
		Project project = projectService.getProject(projectId);
		credentialsValidator.existsUserNameEntered(credentialsForm, credentialsBindingResult);
		if(!credentialsBindingResult.hasErrors()) {
			Credentials credentials = this.credentialsService.getCredential(credentialsForm.getUserName());
			User user= credentials.getUser();	
			this.projectService.shareProjectWithUser(project, user);
			return "projectSharedSuccessful";
		}
		model.addAttribute("projectForm", project);
		model.addAttribute("credentialsForm", credentialsForm);		
		return "shareProject";
	}

	private boolean isProjectOwner(Project project, User owner) {
		return project.getOwner().equals(owner);
	}

	private boolean isProjectMember(Project project, User member) {
		return project.getMembers().contains(member);
	}
}
