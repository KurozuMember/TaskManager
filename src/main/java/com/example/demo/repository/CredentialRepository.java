package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Credentials;


@Repository
public interface CredentialRepository extends CrudRepository<Credentials , Long>{

	public Optional<Credentials> findByUserName(String username);
}
