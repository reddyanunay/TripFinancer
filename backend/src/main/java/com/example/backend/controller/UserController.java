package com.example.backend.controller;

import com.example.backend.Exception.UserAlreadyExistsException;
import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userv;

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody User u) throws UserAlreadyExistsException {
        User u1=userv.addUser(u);
        return new ResponseEntity<>(u1, HttpStatus.CREATED);
    }
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) throws UserNotFoundException {
        User u=userv.getUser(email);
        return new ResponseEntity<>(u,HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(userv.getallUsers(),HttpStatus.OK);
    }
}
