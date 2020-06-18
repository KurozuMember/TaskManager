package com.example.demo.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.controller.session.SessionData;
import com.example.demo.controller.validation.CredentialsValidator;
import com.example.demo.controller.validation.TaskValidator;
import com.example.demo.model.Comment;
import com.example.demo.model.Credentials;
import com.example.demo.model.Project;
import com.example.demo.model.Tag;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.services.CommentService;
import com.example.demo.services.CredentialsService;
import com.example.demo.services.ProjectService;
import com.example.demo.services.TaskService;
import com.example.demo.services.UserService;

@Controller
public class TaskController {

	@Autowired
	SessionData sessionData;

	@Autowired
	TaskService taskService;

	@Autowired 
	TaskValidator taskValidator;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	CredentialsValidator credentialsValidator;

	@Autowired
	CommentService commentService;

	//ritorna la pagina del Task
	@RequestMapping(value = { "/projects/{projectId}/tasks/{taskId}" }, method = RequestMethod.GET)
	public String task(Model model, @PathVariable Long taskId, @PathVariable Long projectId) {
		Task task = this.taskService.getTask(taskId);
		Project project = this.projectService.getProject(projectId);
		model.addAttribute("taskForm", task);
		model.addAttribute("projectForm", project);
		//per aggiungere un commento
		model.addAttribute("commentForm", new Comment());
		return "task";
	}

	//L'URL che cattura la richiesta è parametrico: indichiamo il campo parametrico con parentesi graffe
	@RequestMapping(value = { "/projects/{projectId}/addTask" }, method = RequestMethod.GET)
	public String addTask(Model model, @PathVariable Long projectId) { 	//annotiamo con @PathVariable l'oggetto parametrico che ha lo stesso nome dato nell'URL
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);

		System.out.println("PROGETTO: " + project.getId());
		System.out.println("OWNER PROGETTO: " + project.getOwner().toString());
		//System.out.println("OWNER: " + loggedUser);
		//se l'utente loggato non risulta il proprietario
		if(!project.getOwner().equals(loggedUser))		//possiamo fare getOwner perché l'associazione con lo User è EAGER
			return "redirect:/projects/"+projectId;

		model.addAttribute("project", project);
		model.addAttribute("taskForm", new Task());

		return "addTask";
	}

	@RequestMapping(value = { "/projects/{projectId}/addTask" }, method = RequestMethod.POST)
	public String createTask(@Valid @ModelAttribute("taskForm") Task task, @PathVariable Long projectId, BindingResult taskBindingResult, Model model) {
		User loggedUser = sessionData.getLoggedUser();
		taskValidator.validate(task, taskBindingResult);
		if (!taskBindingResult.hasErrors()) {
			Project project = projectService.getProject(projectId);
			project.addTask(task);
			projectService.saveProject(project);
			return "redirect:/projects/" + projectId;				
		}
		model.addAttribute("loggedUser", loggedUser);
		return "addTask";
	}

	//il metodo cattura la richiesta della modifica del task e predispone una form di modifica
	@RequestMapping(value= { "/projects/{projectId}/tasks/update/{taskId}" }, method = RequestMethod.GET)
	public String taskForm(Model model, @PathVariable Long projectId, @PathVariable Long taskId) {
		Task task = taskService.getTask(taskId);
		Project project = projectService.getProject(projectId);
		if(!project.getOwner().equals(sessionData.getLoggedUser())) {
			return "redirect:/projects";
		}
		//serve per caricare i tag del progetto
		model.addAttribute("projectForm", project);
		model.addAttribute("taskForm", task);
		return "taskUpdate";
	}

	@RequestMapping(value = { "/projects/{projectId}/tasks/update/{taskId}" }, method = RequestMethod.POST)
	public String updateTask(@Valid @ModelAttribute("taskForm") Task taskForm, @PathVariable Long taskId, @PathVariable Long projectId,
			@RequestParam(value = "tagsId", required=false) List<Long> tagsId, BindingResult taskBindingResult, Model model) {

		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		if(!project.getOwner().equals(loggedUser)) {
			return "redirect:/projects";
		}
		taskValidator.validate(taskForm, taskBindingResult);
		if (!taskBindingResult.hasErrors()) {

			Task task = this.taskService.getTask(taskId);
			task.setDescription(taskForm.getDescription());
			task.setName(taskForm.getName());
			task.setTags(taskForm.getTags());
			task.setCompleted(taskForm.getCompleted());
			//ho catturato gli id dei tag nella form e li uso per prendere i tag dal project 
			//(vedi @RequestParam tagsId che è il nome della checkbox che cattura i dati nella form)
			//un @RequestParam può catturare diverse cose, tra cui i campi delle form
			if(tagsId!=null) {
				List<Tag> projectTags = project.getTags();
				for(Long tagId : tagsId) {
					for(Tag tag : projectTags) {
						if(tagId == tag.getId()) {
							task.getTags().add(tag);
						}
					}
				}
			}
			this.taskService.saveTask(task);
			return "taskUpdateSuccessful";
		}
		model.addAttribute("taskForm", taskForm);
		model.addAttribute("projectForm", project);
		return "taskUpdate";
	}

	//il metodo cattura la richiesta dell'assegnazione del task e predispone una form di assegnazione
	@RequestMapping(value= { "/projects/{projectId}/tasks/assign/{taskId}" }, method = RequestMethod.GET)
	public String assignTask(Model model, @PathVariable Long projectId, @PathVariable Long taskId) {
		Project project = projectService.getProject(projectId);
		if(!project.getOwner().equals(sessionData.getLoggedUser())) {
			return "redirect:/projects";
		}
		model.addAttribute("credentialsForm", new Credentials());
		return "assignTask";
	}

	//il metodo cattura la richiesta della modifica del task e predispone una form di modifica
	@RequestMapping(value= { "/projects/{projectId}/tasks/assign/{taskId}" }, method = RequestMethod.POST)
	public String assignForm(Model model, @Valid @ModelAttribute ("credentialsForm") Credentials credentialsForm, BindingResult credentialsBindingResult, @PathVariable Long projectId,  @PathVariable Long taskId) {
		User loggedUser = sessionData.getLoggedUser();
		System.out.println("ID PATH POST: " + projectId);
		Project project = projectService.getProject(projectId);
		System.out.println("ID PROJECT POST: " + project.getId());
		if(!project.getOwner().equals(loggedUser)) {
			return "redirect:/projects";
		}
		//verifico che lo username esista
		credentialsValidator.existsUserNameEntered(credentialsForm, credentialsBindingResult);
		if(!credentialsBindingResult.hasErrors()) {
			//verifico che lo username si riferisca a un membro del progetto
			credentialsValidator.refersToProjectMember(credentialsForm, project, credentialsBindingResult);
			if(!credentialsBindingResult.hasErrors()) {
				Credentials credentials = this.credentialsService.getCredential(credentialsForm.getUserName());
				User user= credentials.getUser();
				Task task = this.taskService.getTask(taskId);
				task.setAssignee(user);
				this.taskService.saveTask(task);
				return "taskAssignSuccessful";
			}
		}
		return "assignTask";
	}

	@RequestMapping(value = { "/projects/{projectId}/tasks/addComment/{taskId}" }, method = RequestMethod.POST)
	public String addComment(@ModelAttribute("commentForm") Comment comment, @PathVariable Long projectId, @PathVariable Long taskId, BindingResult tagBindingResult, Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		Task task = taskService.getTask(taskId);
		if(!(project.getMembers().contains(loggedUser)) && !(project.getOwner().equals(loggedUser))) {
			return "redirect:/projects/" + projectId + "/tasks";
		}
		comment.setPubisher(loggedUser);
		this.commentService.saveComment(comment);
		task.getComments().add(comment);
		this.taskService.saveTask(task);
		return "redirect:/projects/" + projectId + "/tasks/" + taskId;				
	}

	@RequestMapping(value = { "projects/{projectId}/tasks/delete/{taskId}" }, method = RequestMethod.GET)
	public String confirmDeleteTask(Model model, @PathVariable Long taskId, @PathVariable Long projectId) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		if(!project.getOwner().equals(loggedUser)) {
			return "redirect:/projects";
		}
		Task task = taskService.getTask(taskId);
		//this.taskService.deleteTask(task);
		project.getTasks().remove(task);
		this.projectService.saveProject(project);
		return "deleteTask";
	}
}
