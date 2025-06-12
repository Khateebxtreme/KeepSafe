package com.keepsafe.notes.security.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    //It's a DTO class for Login Requests.
    //This class defines the format in which the request is supposed to be sent from frontend to server. Here, The request has two values -> user's username and password coming from the frontend for authentication purposes.
    //If we need some more data from the Login request, we can implement those parameters here in the DTO class making it easily scaleable.
    private String username;

    private String password;

}
