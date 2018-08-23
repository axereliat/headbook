package com.headbook.web.controllers;

import com.headbook.domain.entities.Post;
import com.headbook.domain.entities.User;
import com.headbook.domain.enums.RelationshipStatus;
import com.headbook.repository.RelationshipRepository;
import com.headbook.service.PostService;
import com.headbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PostService postService;

    private final UserService userService;

    private final RelationshipRepository relationshipRepository;

    @Autowired
    public HomeController(PostService postService, UserService userService, RelationshipRepository relationshipRepository) {
        this.postService = postService;
        this.userService = userService;
        this.relationshipRepository = relationshipRepository;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        List<Post> posts = new ArrayList<>();

        List<User> friends = this.relationshipRepository.findAll().stream()
                .filter(x -> x.getStatus().equals(RelationshipStatus.ACCEPTED))
                .filter(x -> x.getUserOne().equals(user.getId()) || x.getUserTwo().equals(user.getId()))
                .map(x -> {
                    User user1 = this.userService.findById(x.getUserOne());
                    if (user.getId().equals(x.getUserOne())) {
                        user1 = this.userService.findById(x.getUserTwo());
                    }
                    return user1;
                })
                .collect(Collectors.toList());

        posts.addAll(user.getPosts());
        for (User friend : friends) {
            posts.addAll(friend.getPosts());
        }

        model.addAttribute("posts", posts.stream().sorted((x1, x2) -> x2.getAddedOn().compareTo(x1.getAddedOn())).toArray(Post[]::new));

        return "home/index";
    }
}
