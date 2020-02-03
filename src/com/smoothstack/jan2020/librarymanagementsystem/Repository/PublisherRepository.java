package com.smoothstack.jan2020.librarymanagementsystem.Repository;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.DataRepository;
import com.smoothstack.jan2020.librarymanagementsystem.model.Publisher;

import java.util.Iterator;

public class PublisherRepository extends DataRepository<Publisher> {

    public PublisherRepository(String filename) {
        super(filename);
    }

    public Iterator<Publisher> findAllByNameIgnoreCase(String name) {
        return find(publisher -> publisher.getName().equalsIgnoreCase(name));
    }

}
