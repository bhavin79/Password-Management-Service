package com.bhavom.PasswordManagerService.data;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcUserRepository {
    JdbcClient jdbcClient;

    JdbcUserRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public String signup(UserRepository user) throws Exception{
        try {
            String SQL= "INSERT INTO USERS (username, pass) VALUES (?,?) ";
            int update = jdbcClient.sql(SQL).param(user.username()).param(user.pass()).update();
            if(update != 1) {
                throw new Exception( "Could not sign up");
            }
        } catch (DataAccessException e) {
            throw new Exception("Could not sign up");
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return "user added";
    }

    public Optional<UserRepository> getUser(String username){
        System.out.println("In user jdbc " + username);
        try {
            String SQL = "select * from users where username = :username";
            return jdbcClient.sql(SQL).param("username",username).query(UserRepository.class).optional();
        }catch (DataAccessException e){
            System.out.println("JDBC USER "+e.getMessage());
        }
        return Optional.empty();
    }


    public String login(UserRepository user){

        try {
            String SQL = "SELECT * FROM USERS WHERE username:username";
            Optional<UserRepository> useExist =  jdbcClient.sql(SQL).param("username", user.username()).query(UserRepository.class).optional();
            if(useExist.isEmpty()){
                return "User not found";
            }
        } catch (DataAccessException e) {
            return e.getMessage();
        }
        return "You are logged in";
    }




}
