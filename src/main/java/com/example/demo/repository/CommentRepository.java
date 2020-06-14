package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Comment;
import com.example.demo.model.User;

public interface CommentRepository extends CrudRepository<Comment, Long>{

	public List<Comment> findByPublisher(User publisher);
}
