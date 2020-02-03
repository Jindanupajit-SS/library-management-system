package com.smoothstack.jan2020.librarymanagementsystem.Repository;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.DataRepository;
import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.Entity;
import com.smoothstack.jan2020.librarymanagementsystem.model.Author;
import com.smoothstack.jan2020.librarymanagementsystem.model.Book;
import com.smoothstack.jan2020.librarymanagementsystem.model.Publisher;

import java.util.HashMap;
import java.util.Map;

public abstract class Repository {

    private static final Map<Class<? extends Entity>, DataRepository<? extends Entity>> dataRepositoryMap = new HashMap<>();

    static {
        dataRepositoryMap.put(Book.class, new BookRepository("resources/data/book"));
        dataRepositoryMap.put(Publisher.class, new PublisherRepository("resources/data/publisher"));
        dataRepositoryMap.put(Author.class, new AuthorRepository("resources/data/author"));
    }

    public static DataRepository<? extends Entity> getRepository(Class<? extends Entity> entityClass) {
        return dataRepositoryMap.get(entityClass);
    }

    public static void dumpHEX() {
        dataRepositoryMap.forEach((k,v)->v.dumpHEX());
    }

}
