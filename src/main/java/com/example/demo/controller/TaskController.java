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
import com.example.demo.service.ProjectService;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;

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
		@RequestMapping(value = { "addTask" }, method = RequestMethod.GET)
		public String addTask(Model model, @Valid @ModelAttribute("taskForm") Task task, @PathVariable Long projectId, BindingResult taskBindingResult) { 	//annotiamo con @PathVariable l'oggetto parametrico che ha lo stesso nome dato nell'URL
			User loggedUser = sessionData.getLoggedUser();
			Project project = projectService.getProject(projectId);

			//se l'utente loggato non risulta il proprietario
			if(!project.getOwner().equals(loggedUser))		//possiamo fare getOwner perché l'associazione con lo User è EAGER
				return "redirect:/projects/"+projectId;              

			model.addAttribute("project", project);
			model.addAttribute("loggedUser", loggedUser);
			model.addAttribute("taskForm", new Task());

			return "addTask";
		}
}
