package com.headbook.service;

import com.headbook.domain.entities.Role;
import com.headbook.domain.entities.User;
import com.headbook.domain.exceptions.ResourceNotFoundException;
import com.headbook.domain.models.UserRegisterBindingModel;
import com.headbook.repository.RoleRepository;
import com.headbook.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void register(UserRegisterBindingModel bindingModel) {
        if (this.roleRepository.findAll().size() == 0) {
            Role userRole = new Role();
            userRole.setAuthority("USER");
            Role adminRole = new Role();
            adminRole.setAuthority("ADMIN");
            this.roleRepository.saveAndFlush(userRole);
            this.roleRepository.saveAndFlush(adminRole);
        }

        User user = this.modelMapper.map(bindingModel, User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(bindingModel.getPassword()));

        final List<Role> all = this.roleRepository.findAll();

        if (this.userRepository.findAll().size() == 0) {
            user.addRole(this.roleRepository.findByAuthority("USER"));
            user.addRole(this.roleRepository.findByAuthority("ADMIN"));
        } else {
            user.addRole(this.roleRepository.findByAuthority("USER"));
        }

        //user.setAvatar("http://res.cloudinary.com/dr8ovbzd2/image/upload/v1534048322/no-user.png");
        user.setAvatar("https://ui-avatars.com/api/?background=0D8ABC&color=fff&name=" + user.getFirstName() + "+" + user.getLastName());

        this.userRepository.saveAndFlush(user);
    }

    @Override
    public User findById(Integer id) {
        Optional<User> user = this.userRepository.findById(id);

        if (!user.isPresent()) {
            throw new ResourceNotFoundException();
        }

        return user.get();
    }

    @Override
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public void save(User user) {
        this.userRepository.saveAndFlush(user);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(s);

        if (user == null) {
            throw new UsernameNotFoundException("Wrong email");
        }

        return user;
    }
}
