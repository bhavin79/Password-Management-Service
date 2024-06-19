package com.bhavom.PasswordManagerService.data;

public record UserRepository(
        long id,
        String username,
        String pass
) {

}
