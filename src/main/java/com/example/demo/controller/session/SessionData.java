package com.example.demo.controller.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.demo.model.Credentials;
import com.example.demo.model.User;
import com.example.demo.repository.CredentialRepository;

@Component
@Scope(value= "session",proxyMode=ScopedProxyMode.TARGET_CLASS)
/*Oggetto che ah uno scope ossia una visione limitata alla singola sessione*/
public class SessionData {
private User user;
private Credentials credentials;

@Autowired 
private CredentialRepository credentialsRepository;
/*In questo  modo quando vogliamo ottenere le credenziali dal database dell'  utente attualmente loggato*/
/*o l' utenteloggato come oggetto user..*/
public Credentials getLoggedCredentials() {
	if(this.credentials==null)
		this.update();
		return this.credentials;
}
public User getLoggedUser() {
	if(this.user==null)
		this.update();
	return this.user;
}
/*Prende lo user details dell' utente autenticato , ne prende la username , va dalla credentialsRepository*/
/*setta la password  a protected per motivi di sicurezza (non vogliamo avere nulla a che fare con le password)
 *e poi setta this.credential con le credenziali recuperate e this.user con lo user delle credenziali recuperate
 *quindi essendo  sessionData component possiamo aggiungerlo ovunque ce ne sia bisogno con la notazione @Autowired minimizzando  l' accesso al database */
private void update() {
	Object obj=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	UserDetails loggedUserDetails=(UserDetails)obj;
	this.credentials=this.credentialsRepository.findByUserName(loggedUserDetails.getUsername()).get();
	this.credentials.setPassword("[PROTECTED]");
	this.user=this.credentials.getUser();
}
public void setLoggedUser(User user) {
	this.user=user;
}
public void setCredentials(Credentials credentials) {
	this.credentials=credentials;
}
	
}
