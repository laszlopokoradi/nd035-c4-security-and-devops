package com.example.demo.model.requests;


import com.fasterxml.jackson.annotation.*;


public class CreateUserRequest {

    @JsonProperty
    private String username;

	@JsonProperty
	private String password;

    @JsonProperty()
    private String repeatedPassword;


    public String getUsername() {
		return username;
	}

    public CreateUserRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CreateUserRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isPasswordValid() {
        return password != null && password.equals(repeatedPassword) && password.length() >= 8;
    }
}
