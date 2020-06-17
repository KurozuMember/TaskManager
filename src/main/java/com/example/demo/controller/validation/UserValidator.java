package com.example.demo.controller.validation;



import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.model.Project;
import com.example.demo.model.User;

@Component
public class UserValidator  implements Validator{
	final Integer MAX_NAME_LENGTH=100;
	final Integer MIN_NAME_LENGT=2;
	@Override
	public void validate(Object o,Errors errors) {
		User user = (User)o;
		String firstname=user.getFirstName().trim();
		String lastname= user.getLastName().trim();
		if(firstname.trim().isEmpty())
			errors.rejectValue("firstName", "required");
		else if(firstname.length()<MIN_NAME_LENGT||firstname.length()>MAX_NAME_LENGTH)
			errors.rejectValue("firstName","size");
		if(lastname.trim().isEmpty())
			errors.rejectValue("lastName", "required");
		else if(lastname.length()<MIN_NAME_LENGT||lastname.length()>MAX_NAME_LENGTH)
			errors.rejectValue("lastName","size");
	}
	
	@Override
	public boolean supports(Class<?>clazz) {
		return User.class.equals(clazz);
	}

}
