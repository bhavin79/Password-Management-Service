package com.bhavom.PasswordManagerService.data;

public record PasswordRepository(
        long id,
        String title,
        String username,
        String pass,
        Long userid
) {
    public PasswordRepository(long id, String title, String username, String pass, Long userid) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.pass = pass;
        this.userid = userid;
    }

    // Setter method for userid
    public PasswordRepository setUserId(long userid) {
        return new PasswordRepository(id, title, username, pass, userid);
    }

    // Setter method for password
    public PasswordRepository setPassword(String password) {
        return new PasswordRepository(id, title, username, password, userid);
    }


}
