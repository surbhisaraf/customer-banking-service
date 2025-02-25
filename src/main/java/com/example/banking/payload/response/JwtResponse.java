package com.example.banking.payload.response;


import java.io.Serializable;

public class JwtResponse implements Serializable {
    private String token;
    private String type;
   // private String id;
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // private String email;
    private String role;

	public JwtResponse(String token, String username, String role) {
            this.token = token;
            this.type = "Bearer ";
            this.username=username;
            this.role = role;
        }


}
