package com.example.demo.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.model.Task;

@Component
public class TaskValidator implements Validator {

	final Integer MAX_NAME_LENGTH = 100;
	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_DESCRIPTION_LENGTH = 1000;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Task.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Task task = (Task)o;
		String name = task.getName().trim();
		String description = task.getDescription().trim();

		if (name.isEmpty()) {
			errors.rejectValue("name","required");
		}
		else if(name.length()<MIN_NAME_LENGTH || name.length()>MAX_NAME_LENGTH) {
			errors.rejectValue("name", "size");
		}
		if(description.length()>MAX_DESCRIPTION_LENGTH) {
			errors.rejectValue("description", "size");
		}		
	}

	
}
