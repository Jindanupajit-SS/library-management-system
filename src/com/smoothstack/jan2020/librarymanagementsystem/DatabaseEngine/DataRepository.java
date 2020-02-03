package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

// TODO : Iterator, Search


public abstract class DataRepository<T extends Entity> implements Closeable {

    private String filename;
    private DataAccess<T> dataAccess;
    private IndexFile indexFile;
    private DataFile dataFile;

    public DataRepository(String filename) {

        this.filename = filename;

        try {
            this.indexFile = new IndexFile(filename);
            this.dataFile = new DataFile(filename);

            IndexTable indexTable = new IndexTable(indexFile);
            this.dataAccess = new DataAccess<>(dataFile, indexTable);
        } catch (IOException e) {
            System.err.println("Cannot connect to database");
            System.exit(1);
        }

    }

    public void save(T object) {
        dataAccess.save(object);
    }

    public T get(long key) {
        return dataAccess.get(key);
    }

    public T delete(long key) {
        return dataAccess.delete(key);
    }

    public Iterator<T> findAll() {
        return find();
    }

    public Optional<T> findById(long id) {
        return Optional.ofNullable(get(id));
    }

    @Override
    public void close() throws IOException {
        this.indexFile.close();
        this.dataFile.close();
    }

    public String getFilename() {
        return filename;
    }

    protected Iterator<T> find() {
        return new DataIterator<T>(dataAccess, null);
    }

    public Iterator<T> find(Predicate<? super T> filter) {
        return new DataIterator<T>(dataAccess, filter);
    }

    public void dumpHEX() {
        try (FileWriter fileWriter = new FileWriter(filename+"_dump.txt", false);
             PrintWriter printWriter = new PrintWriter(fileWriter);) {
            printWriter.println("# Java Data Dump file \n");
            printWriter.printf("#  Original: %s.*\n\n",filename);
            findAll().forEachRemaining(entity->printWriter.println(entity.dump()));

            this.dataAccess.dumpHEX(this.filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


