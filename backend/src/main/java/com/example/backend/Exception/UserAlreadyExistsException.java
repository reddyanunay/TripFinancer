package com.example.backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT,reason = "This User already Exists")
public class UserAlreadyExistsException extends Exception{
}
