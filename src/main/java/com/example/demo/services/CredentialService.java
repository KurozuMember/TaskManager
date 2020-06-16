package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Credentials;
import com.example.demo.repository.CredentialRepository;

@Service
public class CredentialService {
	@Autowired
	protected CredentialRepository credentialRepository;
	/*NOTA:mentre il framework gestisce a livello di login l'encoding della password inserita per confrontarla con quella nel database*/
	/*	   questa non viene altrettanto gestita in modo automatico nel momento della registrazione.*/
	@Autowired
	PasswordEncoder passwordEncoder;
	
	 @Transactional
	    public List<Credentials> getAllCredentials() {
	        List<Credentials> result = new ArrayList<>();
	        Iterable<Credentials> iterable = this.credentialRepository.findAll();
	        for(Credentials credentials : iterable)
	            result.add(credentials);
	        return result;
	    }

	@Transactional
	public Credentials getCredential(long id) {
		Optional<Credentials> result=this.credentialRepository.findById(id);
		return result.orElse(null);
	}
	@Transactional
	public Credentials getCredential(String username) {
		Optional<Credentials> result=this.credentialRepository.findByUserName(username);
		return result.orElse(null);
	}
	@Transactional
	public Credentials saveCredential(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialRepository.save(credentials);
	}
	@Transactional
	public Credentials updateCredential(Credentials credentials) {
		return this.credentialRepository.save(credentials);
	}
}
