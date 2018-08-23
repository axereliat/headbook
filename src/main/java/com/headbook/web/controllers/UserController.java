package com.headbook.web.controllers;

import com.headbook.domain.entities.Relationship;
import com.headbook.domain.entities.User;
import com.headbook.domain.enums.RelationshipStatus;
import com.headbook.domain.models.UserRegisterBindingModel;
import com.headbook.repository.RelationshipRepository;
import com.headbook.service.CloudService;
import com.headbook.service.UserService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authManager;

    private final RelationshipRepository relationshipRepository;

    private final CloudService cloudService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authManager, RelationshipRepository relationshipRepository, CloudService cloudService) {
        this.userService = userService;
        this.authManager = authManager;
        this.relationshipRepository = relationshipRepository;
        this.cloudService = cloudService;
    }

    private void autoLogin( String username, String password, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = this.authManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication );

        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @PostMapping("/avatarUpload")
    public String avatarUpload(@RequestParam MultipartFile avatar, RedirectAttributes redirectAttributes, Principal principal) throws IOException {
        User currentUser = this.userService.findByEmail(principal.getName());
        currentUser.setAvatar(this.cloudService.upload(avatar));

        redirectAttributes.addFlashAttribute("success", "Avatar updated!");

        return "redirect:/profile/" + currentUser.getId();
    }

    @PostMapping(value = "/register", produces = "application/json")
    public @ResponseBody Map<String, String> register(UserRegisterBindingModel bindingModel, HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("error", "");

        if (this.userService.findByEmail(bindingModel.getEmail()) != null) {
            map.put("error", "Email is already taken.");
        }
        if (!bindingModel.getPassword().equals(bindingModel.getConfirmPassword())) {
            map.put("error", "Passwords do not match.");
        }
        if (bindingModel.getEmail().equals("") || bindingModel.getPassword().equals("") || bindingModel.getConfirmPassword().equals("")
                || bindingModel.getFirstName().equals("") || bindingModel.getLastName().equals("")) {
            map.put("error", "Please fill in all fields.");
        }

        if (map.get("error").equals("")) {
            this.userService.register(bindingModel);
            this.autoLogin(bindingModel.getEmail(), bindingModel.getPassword(), request);
        }

        return map;
    }

    @GetMapping("/logout")
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }

    @GetMapping("/profile/{id}")
    public String profile(Model model, @PathVariable Integer id, Principal principal) {
        User otherUser = this.userService.findById(id);
        User currentUser = this.userService.findByEmail(principal.getName());

        RelationshipStatus status = null;
        Relationship relationship = this.relationshipRepository.findByUserOneAndUserTwo(currentUser.getId(), otherUser.getId());

        model.addAttribute("userId", otherUser.getId());
        model.addAttribute("relationship", relationship);
        model.addAttribute("user", otherUser);

        return "user/profile";
    }

    @GetMapping(value = "addFriend", produces = "application/json")
    public @ResponseBody void addFriend(Integer userId, Principal principal) {
        Relationship relationship = new Relationship();
        Integer currentUserId = this.userService.findByEmail(principal.getName()).getId();
        Integer otherUserId = this.userService.findById(userId).getId();

        relationship.setUserOne(currentUserId);
        relationship.setUserTwo(otherUserId);
        relationship.setActionUser(currentUserId);
        relationship.setStatus(RelationshipStatus.PENDING);

        this.relationshipRepository.saveAndFlush(relationship);
    }

    @GetMapping(value = "cancel", produces = "application/json")
    public @ResponseBody void cancelFriend(Integer userId, Principal principal) {
        Integer currentUserId = this.userService.findByEmail(principal.getName()).getId();
        Integer otherUserId = this.userService.findById(userId).getId();
        Relationship relationship = this.relationshipRepository.findByUserOneAndUserTwo(currentUserId, otherUserId);

        this.relationshipRepository.delete(relationship);
    }

    @GetMapping(value = "decline", produces = "application/json")
    public @ResponseBody void declineFriend(Integer userId, Principal principal) {
        Integer currentUserId = this.userService.findByEmail(principal.getName()).getId();
        Integer otherUserId = this.userService.findById(userId).getId();
        Relationship relationship = this.relationshipRepository.findByUserOneAndUserTwo(currentUserId, otherUserId);

        this.relationshipRepository.delete(relationship);
    }

    @GetMapping(value = "accept", produces = "application/json")
    public @ResponseBody void acceptFriend(Integer userId, Principal principal) {
        Integer currentUserId = this.userService.findByEmail(principal.getName()).getId();
        Integer otherUserId = this.userService.findById(userId).getId();
        Relationship relationship = this.relationshipRepository.findByUserOneAndUserTwo(currentUserId, otherUserId);
        relationship.setStatus(RelationshipStatus.ACCEPTED);
        relationship.setActionUser(currentUserId);

        this.relationshipRepository.saveAndFlush(relationship);
    }

    @GetMapping(value = "unfriend", produces = "application/json")
    public @ResponseBody void unfriend(Integer userId, Principal principal) {
        Integer currentUserId = this.userService.findByEmail(principal.getName()).getId();
        Integer otherUserId = this.userService.findById(userId).getId();
        Relationship relationship = this.relationshipRepository.findByUserOneAndUserTwo(currentUserId, otherUserId);

        this.relationshipRepository.delete(relationship);
    }

    @GetMapping("/friendRequests")
    public String friendRequests() {
        return "user/friendRequests";
    }
}
