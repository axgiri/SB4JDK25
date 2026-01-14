package com.example.demo;

import org.jspecify.annotations.NullMarked;

@NullMarked
record Branch(String name, Commit commit) {
}
