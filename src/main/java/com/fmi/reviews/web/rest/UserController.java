package com.fmi.reviews.web.rest;

import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.model.User;
import com.fmi.reviews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

import java.util.List;

import static com.fmi.reviews.utils.ErrorHandlingUtils.getViolationsAsStringList;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user, Errors errors){
        if (errors.hasErrors()) {
            throw new InvalidEntityDataException("Invalid user data", getViolationsAsStringList(errors));
        }

        User newUser = userService.addUser(user);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                        .buildAndExpand(newUser.getId()).toUri()
        ).body(newUser);
    }

    @PutMapping("/{id}")
    public  User updateUser(@Valid @RequestBody User user, @PathVariable Long id, Errors errors){
        if (errors.hasErrors()) {
            throw new InvalidEntityDataException("Invalid user data", getViolationsAsStringList(errors));
        }

        return userService.updateUser(id, user);
    }
}
