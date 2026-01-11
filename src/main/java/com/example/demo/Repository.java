package com.example.demo;

import java.util.List;

record Repository(String name, Owner owner, boolean fork, List<Branch> branches) {

    Repository withBranches(List<Branch> branches) {
        return new Repository(name, owner, fork, branches);
    }
}
