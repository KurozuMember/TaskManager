package com.example.demo.controller.validation;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.model.Credentials;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.services.CredentialsService;

@Component
public class CredentialsValidator implements Validator {

	@Autowired
	CredentialsService credentialsService;

	final Integer MAX_USERNAME_LENGTH=20;
	final Integer MIN_USERNAME_LENGTH=4;
	final Integer MAX_PASSWORD_LENGTH=28;
	final Integer MIN_PASSWORD_LENGTH=6;
	@Override 
	public void validate(Object o,Errors errors) {
		Credentials credentials =(Credentials)o;
		String username=credentials.getUserName();
		String password=credentials.getPassword();
		if(username.trim().isEmpty()) {
			errors.rejectValue("userName","required");
		}
		else if (username.length()<MIN_USERNAME_LENGTH||username.length()>MAX_USERNAME_LENGTH) {
			errors.rejectValue("userName","size");
		}
		else if(this.credentialsService.getCredential(username)!=null) {
			errors.rejectValue("userName", "duplicate");
		}
		if(password.trim().isEmpty()) {
			errors.rejectValue("password","required");
		}
		else if (password.length()<MIN_PASSWORD_LENGTH||password.length()>MAX_PASSWORD_LENGTH) {
			errors.rejectValue("password","size");
		}
	}

	public void validateM(Object o,Errors errors) {
		Credentials credentials =(Credentials)o;
		String username=credentials.getUserName();
		String password=credentials.getPassword();

		if(username.trim().isEmpty()) {
			errors.rejectValue("userName","required");
		}
		else if (username.length()<MIN_USERNAME_LENGTH||username.length()>MAX_USERNAME_LENGTH) {
			errors.rejectValue("userName","size");
		}
		if ((password.length()<MIN_PASSWORD_LENGTH||password.length()>MAX_PASSWORD_LENGTH)&&(!password.trim().isEmpty())) {
			errors.rejectValue("password","size");
		}
	}

	public void existsUserNameEntered(Object o, Errors errors) {
		Credentials credentials = (Credentials)o;
		String username = credentials.getUserName();
		if(username.trim().isEmpty()) {
			errors.rejectValue("userName","required");
		}
		else if(this.credentialsService.getCredential(username) == null) {
			errors.rejectValue("userName", "doesntExist");
		}
	}

	public void refersToProjectMember(Object o, Project project, Errors errors) {
		Credentials credentials = (Credentials)o;
		String username = credentials.getUserName();
		User user = this.credentialsService.getCredential(username).getUser();
		if(!project.getMembers().contains(user)) {
			errors.rejectValue("userName", "not_a_member");
		}
	}

	@Override
	public boolean supports(Class<?>clazz) {
		return User.class.equals(clazz);
	}

}
