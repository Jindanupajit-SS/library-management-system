package com.smoothstack.jan2020.librarymanagementsystem.Repository;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.DataRepository;
import com.smoothstack.jan2020.librarymanagementsystem.model.Author;
import com.smoothstack.jan2020.librarymanagementsystem.model.Book;

import java.util.Iterator;

public class BookRepository extends DataRepository<Book> {

    public BookRepository(String filename) {
        super(filename);
    }

    public Iterator<Book> findAllByNameIgnoreCase(String name) {
        return find(book -> book.getName().equalsIgnoreCase(name));
    }

    public Iterator<Book> findAllByNameContainsIgnoreCase(String name) {
        return find(book -> book.getName().toLowerCase().contains(name.toLowerCase()));
    }

    public Iterator<Book> findAllByAuthor(Author author) {
        return find(book -> book.getAuthor().equals(author));
    }
}
