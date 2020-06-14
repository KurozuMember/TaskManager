package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.User;
import com.example.demo.repository.CredentialRepository;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Transactional
	public User getUser(Long id) {
		Optional<User> result=this.userRepository.findById(id);
		return result.orElse(null);
	}
	@Transactional
	public User saveUser(User user) {
		return this.userRepository.save(user);
	}
	@Transactional
	public User updateUser(User user) {
		return this.userRepository.save(user);
	}
	@Transactional
	public List<User> findAllUsers() {
		ArrayList<User> users = new ArrayList<>();
		Iterable<User> i= this.userRepository.findAll();
		for(User u : i){
			users.add(u);
		}
		return users;
	}
}
