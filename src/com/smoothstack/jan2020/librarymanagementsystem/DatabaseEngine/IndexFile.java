package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class IndexFile implements Closeable {

    private static final String fileSignature = "JAVA INDEX FILE";
    private String filename;
    private RandomAccessFile raf;

    public IndexFile(String filename) throws IOException {
        this.filename = filename+".index";

        // Create directory if missing
        File file = new File(this.filename);
        if (! file.exists() && ! file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        raf = new RandomAccessFile(this.filename, "rw");
        if (raf.length() == 0) this.newFile();
    }
    protected void newFile() throws IOException {
        long pos = 0;
        raf.seek(0);
        raf.writeBytes(String.format("%-64s",fileSignature).replace(" ", "\0"));
        pos = raf.getFilePointer();
    }

    public IndexTable deserialize() throws IOException {
        raf.seek(64); // Skip signature
        HashMap<Long, long[]> indexTableHashMap = new HashMap<>();

        long indexPos = 0;
         while ((indexPos = raf.getFilePointer()) <= raf.length() - 16) {
            long key = raf.readLong();
            long dataPos = raf.readLong();
             indexTableHashMap .put(key, new long[]{dataPos, indexPos});
        }
        return new IndexTable(this, indexTableHashMap);

    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    public RandomAccessFile getRaf() {
        return raf;
    }
}
