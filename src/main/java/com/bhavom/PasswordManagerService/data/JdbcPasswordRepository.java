package com.bhavom.PasswordManagerService.data;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPasswordRepository {
    JdbcClient jdbcClient;
    JdbcPasswordRepository(JdbcClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public List<PasswordRepository> getPasswords(long id){
        try{
            String SQL = "SELECT * FROM PASSWORDS WHERE USERID = :id";
            System.out.println(jdbcClient.sql(SQL).param("id", id).query(PasswordRepository.class).list());
            return jdbcClient.sql(SQL).param("id", id).query(PasswordRepository.class).list();
        }catch (DataAccessException e){
            System.out.println( "In password something wrong" + e.getMessage());
        }
        return null;
    }

    public Optional<PasswordRepository> getPassword(String title, long userId){
            String SQL = "select * from passwords where title = ? and userid = ?";
        try {
            return jdbcClient.sql(SQL).param(title).param(userId).query(PasswordRepository.class).optional();
        } catch (DataAccessException e) {
            System.out.println( "Something went wrong "+e.getMessage());
        }
        return Optional.empty();
    }

    public String addPassword(PasswordRepository pass){
        try {
            String SQL = "INSERT INTO PASSWORDS(title, username, pass, userid) VALUES(?,?,?,?)";
            int update = jdbcClient.sql(SQL).param(pass.title()).param(pass.username()).param(pass.pass()).param(1).update();

            if(update != 1){
                return "Could not add";
            }
        }catch (DataAccessException e){
            return "something went wrong" + e.getMessage();
        }

        return "successfully added";
    }

    public String putPassword(PasswordRepository newPass){
        try {
            String SQL = "update passwords SET pass = ?, username=?  WHERE title = ? and userid =?";

            int update = jdbcClient.sql(SQL)
                    .param(newPass.pass())
                    .param(newPass.username())
                    .param(newPass.title())
                    .param(newPass.userid())
                    .update();

            if(update != 1){
                return "Could not update";
            }
        }catch (DataAccessException e){
            return "something went wrong" + e.getMessage();
        }

        return "successfully updated";
    }
    public String removePassword(String title, long userId) throws Exception {
        String SQL = "delete from passwords where title=? and userid = ?";
        try{
            int update = jdbcClient.sql(SQL).param(title).param(userId).update();
            System.out.println(update);
            if(update >0){
                return "Successfully Deleted";
            }
        }catch (DataAccessException e){
            return "something went wrong" + e.getMessage();
        }
        throw new Exception("Item not found");
    }

}
