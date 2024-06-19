package com.bhavom.PasswordManagerService.Srvices;

import com.bhavom.PasswordManagerService.data.JdbcPasswordRepository;
import com.bhavom.PasswordManagerService.data.JdbcUserRepository;
import com.bhavom.PasswordManagerService.data.PasswordRepository;
import com.bhavom.PasswordManagerService.data.UserRepository;
import org.springframework.stereotype.Service;



import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Service
public class PasswordService {
    JdbcUserRepository jdbcUserRepository;
    JdbcPasswordRepository jdbcPasswordRepository;

    PasswordService(JdbcUserRepository jdbcUserRepository, JdbcPasswordRepository jdbcPasswordRepository){
        this.jdbcUserRepository = jdbcUserRepository;
        this.jdbcPasswordRepository = jdbcPasswordRepository;
    }

    public List<Map<String, String>> getPasswords(long userid){
        List<PasswordRepository> passes = jdbcPasswordRepository.getPasswords(userid);
        List<Map<String, String>> finalPass = new ArrayList<>();
        for(PasswordRepository pass: passes){
            if(pass != null){
                finalPass.add(Map.of("title", pass.title()));
            }
        }
        return finalPass;
    }

    public  Optional<Map<String, String>> getPassword(String tittle, String username){
        Optional<UserRepository> userExist = jdbcUserRepository.getUser(username);
        if(userExist.isEmpty()){
            return Optional.empty();
        }

        Optional<PasswordRepository> passwordRepo =  jdbcPasswordRepository.getPassword(tittle, userExist.get().id());
        if(passwordRepo.isEmpty()){
            return Optional.empty();
        }
        byte[] key = generateKey(username);
        String decrypted = "";
        try{
             decrypted = decrypt(passwordRepo.get().pass(), key);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Map<String, String> map = new HashMap<>();
        map.put("username",passwordRepo.get().username());
        map.put("password",decrypted);
        Optional<Map<String, String>> res = Optional.of(map);

        return res;
    }

    public String addPassword(PasswordRepository pass, String username) throws Exception {

        //Get userid
        Optional<UserRepository> userExist = jdbcUserRepository.getUser(username);
        if(userExist.isEmpty()){
            throw new Exception("User not found");

        }


        //check if this title already exists
        String lowerCaseTitle = pass.title().toLowerCase();

        try {
            List<PasswordRepository> passes = jdbcPasswordRepository.getPasswords(userExist.get().id());
            for(PasswordRepository singlePass: passes){
                if(singlePass != null){
                   if(lowerCaseTitle.equals(singlePass.title())){
                       throw new Exception("Title already Exists. Title must be unique");
                   }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        //Add foreign key
        long fk_userid = userExist.get().id();

        //Generate key and Encrypt Password
        String encryptedPassword = "";
        try{
            byte[] key = generateKey(username);
            encryptedPassword = encrypt(pass.pass(), key);
        }catch (Exception e){
            throw new RuntimeException(e);
        }


        PasswordRepository finalObject = new PasswordRepository(0, lowerCaseTitle, username,encryptedPassword, fk_userid);

        try {
            return jdbcPasswordRepository.addPassword(finalObject);
        } catch (Exception e) {
           System.out.println(e.getMessage());
           throw new Exception( "Could not add password");
        }
    }

    public String putPassword(PasswordRepository pass, String username) throws Exception {

        //get user id
        Optional<UserRepository> userExist = jdbcUserRepository.getUser(username);
        if(userExist.isEmpty()){
            throw new Exception("User not found");
        }

        String lowerCaseTitle =  pass.title().toLowerCase();

        // check if same title already exists
        int occurance= 0;
        try {
            List<PasswordRepository> passes = jdbcPasswordRepository.getPasswords(userExist.get().id());
            for(PasswordRepository singlePass: passes){
                System.out.println();
                if(singlePass != null){
                    if(lowerCaseTitle.equals(singlePass.title())){
                        occurance++;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if(occurance ==0 ){
            throw new Exception("Title can't be updated");
        }
        //Add foreign key
        long fk_userid = userExist.get().id();

        //Generate key and Encrypt Password
        String encryptedPassword = "";
        try{
            byte[] key = generateKey(username);
            encryptedPassword = encrypt(pass.pass(), key);
        }catch (Exception e){
           System.out.println(e.getMessage());
            throw new Exception( "Could not update password");

        }

        PasswordRepository finalObject = new PasswordRepository(0, lowerCaseTitle, pass.username(), encryptedPassword, fk_userid);

        try {
            return jdbcPasswordRepository.putPassword(finalObject);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception( "Could not update password");

        }
    }

    public String removePassword(String title, String username) throws Exception {
        Optional<UserRepository> userExist = jdbcUserRepository.getUser(username);
        if(userExist.isEmpty()){
            throw new Exception("User not found");
        }


        return jdbcPasswordRepository.removePassword(title.toLowerCase(), userExist.get().id());
    }



//abcd1234 - key
      byte[] generateKey(String secret){
          try {
              // Get a SHA-256 message digest instance
              MessageDigest md = MessageDigest.getInstance("SHA-256");
              return md.digest(secret.getBytes());

          } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
          }
    }

      String encrypt(String password, byte[] key) throws Exception{

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

     String decrypt(String hash,  byte[] key) throws Exception {

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(hash);
        byte[] decrypt = cipher.doFinal(decoded);
        return new String(decrypt);
    }

}
