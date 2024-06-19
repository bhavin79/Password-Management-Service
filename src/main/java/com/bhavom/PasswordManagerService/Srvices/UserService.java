package com.bhavom.PasswordManagerService.Srvices;

import com.bhavom.PasswordManagerService.data.JdbcUserRepository;
import com.bhavom.PasswordManagerService.data.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import  org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

@Service
public class UserService {
    JdbcUserRepository jdbcUserRepository;

    UserService(JdbcUserRepository jdbcUserRepository){
        this.jdbcUserRepository = jdbcUserRepository;
    }

    public String login(UserRepository user) throws Exception{

        Optional<UserRepository> userExist = jdbcUserRepository.getUser(user.username());
        if(userExist.isEmpty()){
            throw new Exception("Username or password is incorrect");
        }

        if(BCrypt.checkpw(user.pass(), userExist.get().pass())){
            return "Successfully logged in";
        }

        throw new Exception("Username or password is incorrect");

    }


    public String signup(UserRepository user) throws Exception {
        Optional<UserRepository> userExist = jdbcUserRepository.getUser(user.username());
        if(userExist.isPresent()){
            throw new Exception ("Username already taken");
        }

        String password_hash = BCrypt.hashpw(user.pass(), BCrypt.gensalt(6));

        UserRepository newUser = new UserRepository(0, user.username(), password_hash);
        try{
           return jdbcUserRepository.signup(newUser);
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
}
