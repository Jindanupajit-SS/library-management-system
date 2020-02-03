package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

import com.smoothstack.jan2020.librarymanagementsystem.Repository.Repository;

import java.util.NoSuchElementException;

public class OneToOne<T extends Entity> implements RelationToOne<T> {

    private long objRef;
    transient private T objCache;
    private Class<T> dataRepositoryKey;

    public OneToOne(Class<T> entityClass) {
        this.dataRepositoryKey = entityClass;
    }

    @Override
    public T get() {
        if (objRef == 0)
            return null;

        try {
            if (objCache == null)
                objCache = (T) Repository.getRepository(dataRepositoryKey).get(objRef);
        } catch (NoSuchElementException e) {
            objCache = null;
            objRef = 0;
        }

        return objCache;
    }

    @Override
    public void set(T object) {
        if (object == null)
            this.objRef = 0;
        else
            this.objRef = object.getLongId();
        // TODO: Check if equal, or save
    }

    // TODO: Implement Fetch.EAGAR
}
