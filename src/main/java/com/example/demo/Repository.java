package com.example.demo;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
record Repository(String name, Owner owner, boolean fork, @Nullable List<Branch> branches) {

    Repository withBranches(final List<Branch> branches) {
        return new Repository(name, owner, fork, branches);
    }
}
