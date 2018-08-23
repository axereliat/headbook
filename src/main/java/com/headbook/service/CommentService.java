package com.headbook.service;

import com.headbook.domain.entities.Comment;
import com.headbook.domain.entities.User;

public interface CommentService {

    Comment create(String content, Integer postId, User currentUser);

    Comment findById(Integer id);

    void deleteById(Integer id);
}
