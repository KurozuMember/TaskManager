package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.validation.Valid;

import com.example.demo.controller.session.SessionData;
import com.example.demo.controller.validation.CredentialsValidator;
import com.example.demo.controller.validation.UserValidator;
import com.example.demo.model.Credentials;
import com.example.demo.model.User;
import com.example.demo.services.CredentialService;



@Controller
public class AuthenticationController {
	@Autowired
	CredentialService credentialService;
	@Autowired
	UserValidator userValidator;
	@Autowired 
	CredentialsValidator credentialsValidator;
	@Autowired
	SessionData sessionData;

	@RequestMapping(value= {"/users/register"},method=RequestMethod.GET)
	public String showRegisterForm(Model model){
		model.addAttribute("userForm",new User());
		model.addAttribute("credentialsForm", new Credentials());

		return "registerUser";
	}
	 @RequestMapping(value = { "/users/register" }, method = RequestMethod.POST)
	    public String registerUser(@Valid @ModelAttribute("userForm") User user,
	                               BindingResult userBindingResult,
	                               @Valid @ModelAttribute("credentialsForm") Credentials credentials,
	                               BindingResult credentialsBindingResult,
	                               Model model) {

	        // validate user and credentials fields
	        this.userValidator.validate(user, userBindingResult);
	        this.credentialsValidator.validate(credentials, credentialsBindingResult);

	        // if neither of them had invalid contents, store the User and the Credentials into the DB
	        if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
	            // set the user and store the credentials;
	            // this also stores the User, thanks to Cascade.ALL policy
	            credentials.setUser(user);
	            credentialService.saveCredential(credentials);
	            return "registrationSuccessful";
	        }
	        return "registerUser";
	    }
	 
	 
	}
