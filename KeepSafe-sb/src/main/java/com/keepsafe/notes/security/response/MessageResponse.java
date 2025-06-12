package com.keepsafe.notes.security.response;

import lombok.Data;

@Data
public class MessageResponse {
    //generic response class used to send any kind of response to the user.
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
