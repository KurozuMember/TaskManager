package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;

@Service
public class ProjectService {
	
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Transactional
	public Project getProject(long id) {
		Optional<Project> result=this.projectRepository.findById(id);
		return result.orElse(null);
	}
	
	@Transactional
	public Project saveProject(Project project) {
		return this.projectRepository.save(project);
	}
	
	@Transactional
	public void deleteProject(Project project) {
		this.projectRepository.delete(project);
	}
	
	@Transactional
	public Project shareProjectWithUser(Project project,User user) {
		project.addMember(user);
		return this.projectRepository.save(project);
	}
	
	@Transactional
	public List<Project> retrieveProjectsOwnedBy(User loggedUser) {
		return this.projectRepository.findByOwner(loggedUser);
	}
	
	@Transactional
	public List<Project> retrieveProjectsSharedWith(User loggedUser) {
		return this.projectRepository.findByMembers(loggedUser);
	}
}
