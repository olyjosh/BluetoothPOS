package com.forestsoftware.pos.model;

/**
 * Created by HP-PC on 5/24/2018.
 */

public class User
{
    public String username;
    public String password;
    public String vendorId;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(String vendorId)
    {
        this.vendorId = vendorId;
    }
}
