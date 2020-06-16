package com.example.demo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.model.Project;

@Service
public class TaskService {
	@Autowired
	private TaskRepository taskrepository;
	@Autowired
	private ProjectService projectService;
	@Transactional
	public Task getTask(long id ) {
		Optional<Task> result= this.taskrepository.findById(id);
		return result.orElse(null);
	}
	@Transactional 
	public void deleteTask(Task task) {
		this.taskrepository.delete(task);
	}
	@Transactional
	public Task saveTask(Task task) {
		return this.taskrepository.save(task);
	}
	@Transactional
	public Task setCompletedTask(Task task) {
		task.setCompleted(true);
		return this.taskrepository.save(task);
	}
	
	public Task addTaskToProject(Project project, Task task) {
		project.addTask(task);
		this.projectService.saveProject(project);
		return task;
	}
}
