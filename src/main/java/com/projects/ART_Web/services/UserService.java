package com.projects.ART_Web.services;

import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.interfaces.RoleRepository;
import com.projects.ART_Web.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByEmail(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        user.setRoles(Collections.singleton(roleRepository.getByName("ROLE_CLIENT")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);

        String message = String.format(
          "Здравствуйте, %s! \n" +
                  "Добро пожаловать на сайт АРТ-Терапия с Гапоновой Нелли. \n" +
                  "Для подтверждения вашей почты перейдите по ссылке: http://localhost:8080/activate/%s",
                user.getName(), user.getActivationCode()
        );

        emailService.sendEmail(user.getEmail(), "Код подтверждения пользователя", message);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if(user != null){
            user.setActivationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void autoLogin(String username) {
        UserDetails userDetails = loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}