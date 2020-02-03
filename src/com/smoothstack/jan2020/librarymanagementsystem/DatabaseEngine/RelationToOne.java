package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

public interface RelationToOne<T extends Entity> extends Relation<T>{

    T get();
    void set(T object);
    default long getRef() {
        T t = get();

        return t==null?0:get().getLongId();
    }
}
