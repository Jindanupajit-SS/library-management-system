package com.smoothstack.jan2020.librarymanagementsystem.model;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.Entity;
import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.OneToOne;


public class Book implements Entity {

    private long longId;
    private String name;
    private OneToOne<Publisher> publisher = new OneToOne<>(Publisher.class);
    private OneToOne<Author> author = new OneToOne<>(Author.class);

    public Book() {
        this.name = "";
    }

    public Book(String name, Author author, Publisher publisher) {
        this.name = name;
        this.publisher.set(publisher);
        this.author.set(author);

    }

    public Book(String name) {
        this.name = name;

    }

    @Override
    public long getLongId() {
        return longId;
    }

    @Override
    public void setLongId(long longId) {
        this.longId = longId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Publisher getPublisher() {
        return publisher.get();
    }

    public void setPublisher(Publisher publisher) {
        this.publisher.set(publisher);
    }

    public Author getAuthor() {
        return author.get();
    }

    public void setAuthor(Author author) {
        this.author.set(author);
    }

    @Override
    public String dump() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("longId=").append(longId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", publisher=").append(publisher.getRef());
        sb.append(", author=").append(author.getRef());
        sb.append('}');
        return sb.toString();
    }
}
