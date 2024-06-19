package com.bhavom.PasswordManagerService.controller;

import com.bhavom.PasswordManagerService.Srvices.PasswordService;
import com.bhavom.PasswordManagerService.data.PasswordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PasswordController {
    private final PasswordService passwordService;

    PasswordController(PasswordService passwordService){
        this.passwordService = passwordService;
    }


    @GetMapping("/password")
    ResponseEntity<List<Map<String, String>>> getPasswordKeys(){
        List<Map<String, String>> ans = new ArrayList<>();
        return ResponseEntity.ok(passwordService.getPasswords((long) 1));
    }

    @GetMapping("/password/{key}")
    ResponseEntity<Map<String, String>> getPassword(@PathVariable String key){
        Optional< Map<String, String>> password = passwordService.getPassword(key,"user1");
        if(password.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("Error", "Not found"));
        }

        return ResponseEntity.ok(password.get());
    }

    @PostMapping("/password")
    ResponseEntity<String> addPassword(@RequestBody PasswordRepository pass) {
        if(pass.pass() != null && pass.title() != null && pass.username() != null){
            if(!pass.pass().trim().isEmpty() && !pass.username().trim().isEmpty() && !pass.title().trim().isEmpty()){
                try {
                   return ResponseEntity.ok(passwordService.addPassword(pass, "user1"));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }
        }
        return ResponseEntity.badRequest().body("Check inputs. Values can't be null or empty spaces");
    }

    @PutMapping("/password")
    ResponseEntity<String> putPassword(@RequestBody PasswordRepository pass){
        if(pass.pass() != null && pass.title() != null && pass.username() != null){
            if(!pass.pass().trim().isEmpty() && !pass.username().trim().isEmpty() && !pass.title().trim().isEmpty()){
                try {
                   return ResponseEntity.ok(passwordService.putPassword(pass, "user1"));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }
        }
        return ResponseEntity.badRequest().body("Check inputs. Values can't be null or empty spaces");
    }


    @DeleteMapping("/password/{key}")
    ResponseEntity<String> deletePassword(@PathVariable String key){
        try {
            return ResponseEntity.ok(passwordService.removePassword(key, "user1"));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
