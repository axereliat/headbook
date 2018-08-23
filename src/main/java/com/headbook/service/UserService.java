package com.headbook.service;

import com.headbook.domain.entities.User;
import com.headbook.domain.models.UserRegisterBindingModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void save(User user);

    void register(UserRegisterBindingModel bindingModel);

    User findById(Integer id);

    User findByEmail(String email);
}
