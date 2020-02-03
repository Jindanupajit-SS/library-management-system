package com.smoothstack.jan2020.librarymanagementsystem.Repository;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.DataRepository;
import com.smoothstack.jan2020.librarymanagementsystem.model.Author;

import java.util.Iterator;

public class AuthorRepository extends DataRepository<Author> {
    public AuthorRepository(String filename) {
        super(filename);
    }

    public Iterator<Author> findAllByNameIgnoreCase(String name) {
        return find(author -> author.getName().equalsIgnoreCase(name));
    }
}
