package com.example.backend.service;


import com.example.backend.Exception.UserAlreadyExistsException;
import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUser(String email) throws UserNotFoundException {
        User u1 = userRepository.findByEmail(email);
        if(u1==null){
            throw new UserNotFoundException();
        }
        return u1;
    }

    public User addUser(User user) throws UserAlreadyExistsException {
        Optional<User> u1 = userRepository.findById(user.getEmail());
        if(!u1.isEmpty()){
            throw new UserAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    public List<User> getallUsers(){
        return userRepository.findAll();
    }
}
