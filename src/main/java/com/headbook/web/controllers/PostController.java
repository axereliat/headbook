package com.headbook.web.controllers;

import com.headbook.domain.entities.Post;
import com.headbook.domain.entities.User;
import com.headbook.domain.exceptions.AccessDeniedException;
import com.headbook.domain.models.PostCreateBindingModel;
import com.headbook.service.CloudService;
import com.headbook.service.PostService;
import com.headbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public @ResponseBody Map<String, String> create(PostCreateBindingModel bindingModel, MultipartRequest multipartRequest, Principal principal) throws IOException {

        List<MultipartFile> photos = new ArrayList<>();
        for (int i = 0; i < multipartRequest.getMultiFileMap().size(); i++) {
            List<MultipartFile> multipartFiles = multipartRequest.getMultiFileMap().get("file[" + i + "]");
            photos.add(multipartFiles.get(0));
        }

        this.postService.create(bindingModel, photos, principal);

        return new HashMap<>();
    }

    @PostMapping("/create2")
    public @ResponseBody Map<String, String> create(PostCreateBindingModel bindingModel, Principal principal) throws IOException {

        this.postService.create2(bindingModel, principal);

        return new HashMap<>();
    }

    @GetMapping(value = "/like", produces = "application/json")
    public @ResponseBody Map<String, String> like(Integer postId, Principal principal) {
        User user = this.userService.findByEmail(principal.getName());
        Post post = this.postService.findById(postId);
        int likesCount = post.getUsersLiked().size();
        Map<String, String> map = new HashMap<>();
        map.put("hasLiked", "0");
        if (!user.hasLikedPost(post)) {
            user.likePost(post);
            likesCount++;
        } else {
            map.put("hasLiked", "1");
            user.unlikePost(post);
            likesCount--;
        }
        this.userService.save(user);

        map.put("likesCount", String.valueOf(likesCount));

        return map;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Post post = this.postService.findById(id);

        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping(value = "/edit/{id}", produces = "application/json")
    public @ResponseBody void editProcess(PostCreateBindingModel bindingModel, MultipartRequest multipartRequest, @PathVariable Integer id, Principal principal) throws IOException {
        User currentUser = this.userService.findByEmail(principal.getName());
        Post post = this.postService.findById(id);

        if (!currentUser.isAuthor(post)) {
            throw new AccessDeniedException();
        }
        List<MultipartFile> photos = new ArrayList<>();
        for (int i = 0; i < multipartRequest.getMultiFileMap().size(); i++) {
            List<MultipartFile> multipartFiles = multipartRequest.getMultiFileMap().get("file[" + i + "]");
            photos.add(multipartFiles.get(0));
        }

        this.postService.edit(bindingModel, photos, id);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes, Principal principal) {
        User currentUser = this.userService.findByEmail(principal.getName());
        Post post = this.postService.findById(id);
        if (!currentUser.isAuthor(post)) {
            throw new AccessDeniedException();
        }

        this.postService.delete(post);

        redirectAttributes.addFlashAttribute("success", "Post is deleted");
        return "redirect:/";
    }

}
