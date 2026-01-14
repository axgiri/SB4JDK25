package com.example.demo;

import org.jspecify.annotations.NullMarked;

@NullMarked
final class UserNotFoundException extends RuntimeException {

    UserNotFoundException() {
        super("User not found");
    }
}
