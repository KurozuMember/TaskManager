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
import com.example.demo.controller.validation.TaskValidator;
import com.example.demo.model.Project;
import com.example.demo.model.Task;
import com.example.demo.model.User;
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
			model.addAttribute("taskForm", task);
			return "taskUpdate";
		}

		@RequestMapping(value = { "/projects/{projectId}/tasks/update/{taskId}" }, method = RequestMethod.POST)
		public String updateTask(@Valid @ModelAttribute("taskForm") Task taskForm, @PathVariable Long taskId, @PathVariable Long projectId,
				BindingResult taskBindingResult, Model model) {
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
				this.taskService.saveTask(task);
				return "taskUpdateSuccessful";
			}
			return "taskUpdate";
		}

		
		@RequestMapping(value = { "projects/{projectId}/tasks/delete/{taskId}" }, method = RequestMethod.GET)
		public String confirmDeleteTask(Model model, @PathVariable Long taskId, @PathVariable Long projectId) {
			User loggedUser = sessionData.getLoggedUser();
			Project project = projectService.getProject(projectId);
			if(!project.getOwner().equals(loggedUser)) {
				return "redirect:/projects";
			}
			Task task = taskService.getTask(taskId);
			this.taskService.deleteTask(task);
			return "taskDelete";
		}
}
