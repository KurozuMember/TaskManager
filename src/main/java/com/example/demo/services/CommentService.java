package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Comment;
import com.example.demo.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Transactional
	public Comment getComment(Long id) {
		return this.commentRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public Comment saveComment(Comment comment) {
		return this.commentRepository.save(comment);
	}
}
