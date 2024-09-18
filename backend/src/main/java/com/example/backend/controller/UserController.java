package com.example.backend.controller;

import com.example.backend.Exception.UserAlreadyExistsException;
import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping(value = "/api/users", method = RequestMethod.OPTIONS)
public class UserController {
    @Autowired
    private UserService userv;
    @Autowired
    private UserRepository urepo;

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody User u) throws UserAlreadyExistsException {
        User u1=userv.addUser(u);
        return new ResponseEntity<>(u1, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) throws UserNotFoundException
    {
        User u1=urepo.findByEmailAndPassword(u.getEmail(),u.getPassword());
        Map<String,String> map;
        try{
            map=userv.login(u);
        }
        catch (UserNotFoundException a){
            throw new UserNotFoundException();
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("map",map);
        response.put("user",u1);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
//Other approach to check Login but it is not working
//    @CrossOrigin
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User u) {
//        try {
//            // Authenticate user with email and password
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(u.getEmail(), u.getPassword())
//            );
//
//            Map<String,Object> response= new HashMap<>();
//            // If authentication is successful, generate JWT
//            response.put("map",userv.getJWTToken(u));
//            response.put("user",userv.getUser(u.getEmail()));
//
//            // Return the JWT in the response
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (BadCredentialsException e) {
//            // Return an error response for incorrect password
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
//        } catch (UserNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
