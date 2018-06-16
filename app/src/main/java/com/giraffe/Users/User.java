package com.giraffe.Users;

public class User {
    private int user_id;
    private String admin;
    private String pwd;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "User [user_id=" + user_id + ", admin=" + admin + ", pwd=" + pwd + "]";
    }
}

