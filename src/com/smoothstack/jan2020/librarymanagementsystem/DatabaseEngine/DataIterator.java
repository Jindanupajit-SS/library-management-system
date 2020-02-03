package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class DataIterator<T extends Entity> implements Iterator<T> {
    private DataAccess dataAccess;
    private Predicate<? super T> filter;
    private Iterator<Long> keyIterator;
    private long lastKey = 0;

    public DataIterator(DataAccess dataAccess) {
        this(dataAccess, null);

    }

    public DataIterator(DataAccess dataAccess, Predicate<? super T> filter) {
        this.dataAccess = dataAccess;
        this.filter = filter;

        this.keyIterator = (Iterator<Long>) dataAccess.keySet().iterator();
    }


    public void setFilter(Predicate<? super T> filter) {
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {

//        if (this.filter == null) {
//            return keyIterator.hasNext();
//        }

        if (this.lastKey != 0)
            return true;

        if (!keyIterator.hasNext())
            return false;

        T object = null;

        try {
            lastKey = keyIterator.next();
            object = (T) dataAccess.get(lastKey);
        } catch (NoSuchElementException e) {
            // Deleted Object
            this.lastKey = 0;
            return hasNext();
        }

        // Not pass the filter test, go to next element
        if (object == null || (filter != null && !filter.test(object))) {
            lastKey = 0;
            return hasNext();
        }

        return true;
    }

    @Override
    public T next() {
        T object;
        try {
            if (lastKey != 0) {
                object =  (T) dataAccess.get(lastKey);
                lastKey = 0;
            } else {

                object = (T) dataAccess.get(keyIterator.next());
                if (object == null || (filter != null && !filter.test(object))) {

                    lastKey = 0;
                    return next();
                }
            }
        } catch (NoSuchElementException e) {

            return null;
        }

        return object;

    }
}