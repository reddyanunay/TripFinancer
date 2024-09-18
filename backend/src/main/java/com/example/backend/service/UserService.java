package com.example.backend.service;


import com.example.backend.Exception.UserAlreadyExistsException;
import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) throws UserAlreadyExistsException {
        Optional<User> u1 = userRepository.findById(user.getEmail());
        if(!u1.isEmpty()){
            throw new UserAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    public Map<String, String> login(User u) throws UserNotFoundException {
        Map<String,String> token=new HashMap<String,String>();
        try{
            User uu=userRepository.findByEmailAndPassword(u.getEmail(), u.getPassword());
            if(uu!=null)
            {
                token=getJWTToken(u);
            }}
        catch (Exception e){
            throw new UserNotFoundException();
        }
        return token;
    }


    public User getUser(String email) throws UserNotFoundException {
        User u1 = userRepository.findByEmail(email);
        if(u1==null){
            throw new UserNotFoundException();
        }
        return u1;
    }


    public List<User> getallUsers(){
        return userRepository.findAll();
    }

    public Map<String,String> getJWTToken(User u)
    {
        String tok=Jwts.builder().setSubject(u.getEmail()).setIssuedAt(new Date(0)).signWith(SignatureAlgorithm.HS256,"secretkey").compact();
        Map<String,String> tokMap=new HashMap<String,String>();
        tokMap.put("token", tok);
        return tokMap;
    }
}
