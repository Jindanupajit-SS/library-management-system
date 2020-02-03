package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class IndexTable implements Closeable {

    private HashMap<Long, long[]> table = null;
    private IndexFile indexFile  = null;

    public IndexTable(IndexFile indexFile) throws IOException {
        this.indexFile = indexFile;
        this.table = indexFile.deserialize().table;
    }

    public IndexTable(IndexFile indexFile, HashMap<Long, long[]> table) {
        this.indexFile = indexFile;
        this.table = table;
    }

    public boolean containsKey (long key) {
        return table.containsKey(key);
    }

    public long[] find(long key) {
        return table.get(key);
    }

    public long findDataPosition(long key) {
        return find(key)[0];
    }

    public long findIndexPosition(long key) {
        return find(key)[1];
    }

    private void replace(long key, long dataPos) throws IOException {
       long indexPos = findIndexPosition(key);
        table.replace(key, new long[]{dataPos, indexPos});
        if (indexFile != null) {
            indexFile.getRaf().seek(indexPos);
            indexFile.getRaf().skipBytes(8);
            indexFile.getRaf().writeLong(dataPos);
        }
    }

    public void save(long key, long dataPos) throws IOException {
        if (table.containsKey(key)) {

            replace(key, dataPos);
            return;
        }

        long indexPos = indexFile.getRaf().length();

        if (indexFile != null) {
            indexFile.getRaf().seek(indexPos);
            indexFile.getRaf().writeLong(key);
            indexFile.getRaf().writeLong(dataPos);
        } else {
            throw new IOException("No index file");
        }
        table.put(key, new long[]{dataPos, indexPos});
    }

    public long nextId() {
       long maxId = (table.keySet().size()>0)?Collections.max(table.keySet()):0;

       if (maxId < Long.MAX_VALUE)
           return maxId+1;
       else
           throw new IndexOutOfBoundsException("Id was reached the possible maximum value");
    }

    @Override
    public void close() throws IOException {
        indexFile.close();
    }

    public Set<Long> keySet() {
        return table.keySet();
    }
}

