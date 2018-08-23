package com.headbook.service;

import com.headbook.domain.entities.Comment;
import com.headbook.domain.entities.User;
import com.headbook.domain.exceptions.ResourceNotFoundException;
import com.headbook.repository.CommentRepository;
import com.headbook.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment create(String content, Integer postId, User currentUser) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(currentUser);
        comment.setPost(this.postRepository.findById(postId).get());
        comment.setContent(content);

        this.commentRepository.saveAndFlush(comment);

        return comment;
    }

    @Override
    public Comment findById(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (!comment.isPresent()) throw new ResourceNotFoundException();

        return comment.get();
    }

    @Override
    public void deleteById(Integer id) {
        this.commentRepository.deleteById(id);
    }
}
