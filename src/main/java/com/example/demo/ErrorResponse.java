package com.example.demo;

import org.jspecify.annotations.NullMarked;

@NullMarked
record ErrorResponse(int status, String message) {
}
