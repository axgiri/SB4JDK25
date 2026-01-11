package com.example.demo;

class UserNotFoundException extends RuntimeException {

    UserNotFoundException() {
        super("User not found");
    }
}
