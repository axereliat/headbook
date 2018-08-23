package com.headbook.domain.interceptor;

import com.headbook.domain.entities.Relationship;
import com.headbook.domain.entities.User;
import com.headbook.domain.enums.RelationshipStatus;
import com.headbook.repository.RelationshipRepository;
import com.headbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAddInterceptor implements HandlerInterceptor {

    private final UserService userService;

    private final RelationshipRepository relationshipRepository;

    @Autowired
    public UserAddInterceptor(UserService userService, RelationshipRepository relationshipRepository) {
        this.userService = userService;
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication != null) {
            final Object principal = authentication.getPrincipal();
            if (!principal.getClass().equals(String.class)) {
                UserDetails user = (UserDetails) principal;
                User userEntity = this.userService.findByEmail(user.getUsername());

                List<User> relationships = this.relationshipRepository.findAll().stream()
                        .filter(x -> x.getUserOne().equals(userEntity.getId()) || x.getUserTwo().equals(userEntity.getId()))
                        .filter(x -> x.getStatus().equals(RelationshipStatus.PENDING))
                        .map(x -> {
                            Integer userId = x.getActionUser();
                            return this.userService.findById(userId);
                        })
                        .collect(Collectors.toList());

                modelAndView.getModel().put("friendRequests", relationships);
                modelAndView.getModel().put("currentUser", userEntity);
            } else {
                User user = new User();
                modelAndView.getModel().put("currentUser", user);
            }
        }
    }
}
