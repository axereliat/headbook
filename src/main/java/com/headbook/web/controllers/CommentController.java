package com.headbook.web.controllers;

import com.headbook.domain.entities.Comment;
import com.headbook.domain.entities.User;
import com.headbook.service.CommentService;
import com.headbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping(value = "/create/{postId}", produces = "application/json")
    public @ResponseBody Map<String, String> create(@RequestParam String content, @PathVariable Integer postId, Principal principal) {
        User currentUser = this.userService.findByEmail(principal.getName());
        Comment comment = this.commentService.create(content, postId, currentUser);

        Map<String, String> map = new HashMap<>();
        map.put("postId", String.valueOf(postId));
        map.put("comment", content);
        map.put("commentId", String.valueOf(comment.getId()));
        map.put("fullname", currentUser.getFullName());
        map.put("avatar", currentUser.getAvatar());
        map.put("addedOn", comment.getAddedOn().toString());
        map.put("userId", currentUser.getId().toString());

        return map;
    }

    @GetMapping(value = "/delete/{commentId}", produces = "application/json")
    public @ResponseBody Map<String, String> delete(@PathVariable Integer commentId, Principal principal) {
        Comment comment = this.commentService.findById(commentId);

        User currentUser = this.userService.findByEmail(principal.getName());
        if (!currentUser.hasWrittenComment(comment)) {
            return new HashMap<>();
        }

        this.commentService.deleteById(commentId);

        return new HashMap<>();
    }
}
