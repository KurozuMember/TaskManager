package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Project;
import com.example.demo.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long >{
	
	
	
	//public Optional<User> findByUsername(String username);
	public List<User> findByVisibleProjects(Project project);
}
