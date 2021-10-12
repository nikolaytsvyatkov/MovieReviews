package com.fmi.reviews.web.rest;

import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.model.Credentials;
import com.fmi.reviews.model.JwtResponse;
import com.fmi.reviews.model.User;
import com.fmi.reviews.service.UserService;
import com.fmi.reviews.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.fmi.reviews.utils.ErrorHandlingUtils.getViolationsAsStringList;

@RestController
@RequestMapping("/api/")
public class LoginController {

    private UserService userService;
    private JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("login")
    public JwtResponse login(@Valid @RequestBody Credentials credentials, Errors errors){
        if(errors.hasErrors()){
            throw new InvalidEntityDataException("Invalid username or password", getViolationsAsStringList(errors));
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword()));

        User user = userService.getUserByUsername(credentials.getUsername());
        String token = jwtUtils.generateToken(user);

        return new JwtResponse(user, token);
    }

    @PostMapping("register")
    public User register(@Valid @RequestBody User user, Errors errors){
        if(errors.hasErrors()){
            throw new InvalidEntityDataException("Invalid username or password", getViolationsAsStringList(errors));
        }

        return userService.addUser(user);
    }
}
