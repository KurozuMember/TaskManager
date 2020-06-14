package com.example.demo.controller;



import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.controller.session.SessionData;
import com.example.demo.model.Credentials;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.CredentialService;
import com.example.demo.services.UserService;

/**
 * The UserController handles all interactions involving User data.
 */
@Controller
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserValidator userValidator;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	CredentialsValidator credentialsValidator;
	@Autowired
	CredentialService credentialService;
	@Autowired
	SessionData sessionData;
	
	/**
	 * This method is called when a GET request is sent by the user to URL "/users/user_id".
	 * This method prepares and dispatches the User registration view.
	 *
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "register"
	 */
	@RequestMapping(value = { "/home" }, method = RequestMethod.GET)
	public String home(Model model) {
		User loggedUser = this.sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		return "home";
	}

	/**
	 * This method is called when a GET request is sent by the user to URL "/users/user_id".
	 * This method prepares and dispatches the User registration view.
	 *
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "register"
	 */
	@RequestMapping(value = { "/users/me" }, method = RequestMethod.GET)
	public String me(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Credentials credentials = sessionData.getLoggedCredentials();
		System.out.println(credentials.getPassword());
		model.addAttribute("user", loggedUser);
		model.addAttribute("credentials", credentials);

		return "userProfile";
	}
	@RequestMapping(value = { "/users/me/profileUpdater" }, method = RequestMethod.GET)
	public String profileUpdate(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Credentials credentials = sessionData.getLoggedCredentials();
		model.addAttribute("user", loggedUser);
		model.addAttribute("credentials", credentials);

		return "userProfile";
	}


	/**
	 * This method is called when a GET request is sent by the user to URL "/users/user_id".
	 * This method prepares and dispatches the User registration view.
	 *
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "register"
	 */
	@RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
	public String admin(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("user", loggedUser);
		return "admin";
	}

	/*MODIFICA: Ha senso che la modifica dell' utente la faccia direttamente UserController per InformationExpert.
	 * 			usiamo il tag "/users/update" nella pagina userProfile.html dopo le informazioni è presente un link
	 * 			alla pagina di modifica profileUpdate.
	 *NOTA: vedere profileUpdate.html molto simile a registerUser.html
	 *COSA FA?: Prepara modello da passare alla vista ProfileUpdate*/
	@RequestMapping(value= {"/users/update"},method=RequestMethod.GET)
	public String showUpdateForm(Model model){
		model.addAttribute("userForm",sessionData.getLoggedUser());
		model.addAttribute("credentialsForm",new Credentials(sessionData.getLoggedCredentials().getUserName(),null));
		
		return "profileUpdate";
	}
	/*MODIFICA: Ha senso che la modifica dell' utente la faccia direttamente UserController per InformationExpert.
	 * 			usiamo il tag "/users/update" nella pagina userProfile.html dopo le informazioni è presente un link
	 * 			alla pagina di modifica profileUpdate.
	 *COSA FA?: Se le nuove informazioni inserite sono valide viene aggiornata la sessione.*/
	@RequestMapping(value = { "/users/update" }, method = RequestMethod.POST)
	public String updateUser(@Valid @ModelAttribute("userForm") User user,
			BindingResult userBindingResult,
			@Valid @ModelAttribute("credentialsForm") Credentials credentials,
			BindingResult credentialsBindingResult,
			Model model) {
		Credentials cr= credentialService.getCredential(sessionData.getLoggedCredentials().getId());
		// validate user and credentials field
		this.userValidator.validate(user, userBindingResult);
		this.credentialsValidator.validateM(credentials, credentialsBindingResult);
		if(!credentials.getPassword().isBlank()&&!credentials.getPassword().equals(null)) {
			System.out.println(credentials.getPassword());
			cr.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		}
		cr.setUserName(credentials.getUserName());
		// if neither of them had invalid contents, store the User and the Credentials into the DB
		if(!userBindingResult.hasErrors()&&!credentialsBindingResult.hasErrors()) {
			// set the user and store the credentials;
			// this also stores the User, thanks to Cascade.ALL policy

			user.setId(this.sessionData.getLoggedUser().getId());
			this.userService.saveUser(user);
			this.credentialService.updateCredential(cr);
			cr.setPassword("[PROTECTED]");
			this.sessionData.setLoggedUser(user);
			this.sessionData.setCredentials(cr);
			
			
			return "userUpdateSuccessful";
		}
		return "profileUpdate";
	}

}
