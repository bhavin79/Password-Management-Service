package com.bhavom.PasswordManagerService.controller;

import com.bhavom.PasswordManagerService.Srvices.UserService;
import com.bhavom.PasswordManagerService.data.JdbcUserRepository;
import com.bhavom.PasswordManagerService.data.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {
    JdbcUserRepository jdbcUserRepository;
    UserService userService;

    UserController(JdbcUserRepository jdbcUserRepository, UserService userService){
        this.jdbcUserRepository = jdbcUserRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody UserRepository user){
        if(user.pass() == null || user.pass().trim().isEmpty()){
            return ResponseEntity.badRequest().body("password is needed");
        }
        if(user.username() == null || user.username().trim().isEmpty()){
            return ResponseEntity.badRequest().body("username is needed");
        }
        try{
            return ResponseEntity.ok(userService.login(user));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody UserRepository user){
        if(user.pass() == null || user.pass().trim().isEmpty()){
            return ResponseEntity.badRequest().body("password is needed");
        }
        if(user.username() == null || user.username().trim().isEmpty()){
            return ResponseEntity.badRequest().body("username is needed");
        }

        try{
            return ResponseEntity.ok(userService.signup(user));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/logout")
    String logout(){
        return "logged out";
    }

}
