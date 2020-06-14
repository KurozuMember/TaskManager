package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long>{

}
