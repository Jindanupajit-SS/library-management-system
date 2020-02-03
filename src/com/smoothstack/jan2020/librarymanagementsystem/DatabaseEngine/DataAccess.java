package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;


import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class DataAccess<T extends Entity> implements Iterator {

    private DataFile dataFile = null;
    private IndexTable indexTable;

    public DataAccess(DataFile dataFile, IndexTable indexTable) {
        Objects.requireNonNull(dataFile, "dataFile cannot be null");
        Objects.requireNonNull(indexTable, "indexTable cannot be null");

        this.dataFile = dataFile;
        this.indexTable = indexTable;
    }

    public T get(long key){

        if (!indexTable.containsKey(key))
            throw new NoSuchElementException(String.format("Element id = %d",key));

        // locate the data using index
        long dataPos = indexTable.findDataPosition(key);

        // Not found, return null
        if (dataPos == 0)
            throw new NoSuchElementException(String.format("Element id = %d",key));

        // return null if IOException
        try {

            // point raf to the location
            dataFile.seek(dataPos);

            // read ByteArray of serialized Object, and deserialize
            // cast to T and return

            T t = deserialize(dataFile.readByteArrayObject());
            if (t == null)
                throw new NoSuchElementException(String.format("Element id = %d",key));
            return t;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public void save(T object) {

        Objects.requireNonNull(object);

        long key = object.getLongId();

        // not a new object
        if (indexTable.containsKey(key)) {
            replace(object);
            return;
        }

        if (object.getLongId() != 0) {
            throw new NoSuchElementException(String.format("Element id = %d, new element must has id = 0",key));
        }

        try {
            // Seek to EOF and save fp
            dataFile.seekEOF();
            long pos = dataFile.getRaf().getFilePointer();

            object.setLongId(indexTable.nextId());

            indexTable.save(
                    // key
                    object.getLongId(),
                    // fp will be return from dataFile.writeByteArrayObject(bos)
                    dataFile.writeByteArrayObject(serialize(object))
                    );

        } catch (IOException e) {
            // if exception in any case, set the key back to 0
            object.setLongId(0L);
            throw new UncheckedIOException(e);
        }

    }

    public void replace (T object) {

        Objects.requireNonNull(object);

        if (!indexTable.containsKey(object.getLongId())) {
            throw new IndexOutOfBoundsException("Cannot replace, Object.getLongId() return id that is not in the index table");
        }

        long oldPos = 0;
        int oldChunkSize = 0;

        // Create restore point
        try {
            dataFile.seek(indexTable.findDataPosition(object.getLongId()));
            oldPos = dataFile.getRaf().getFilePointer();
            oldChunkSize = dataFile.getChunkSize();
        } catch (IOException e) {
            System.err.println(oldPos);
            System.err.println(oldChunkSize);
            throw new UncheckedIOException(e);
        }



        try {
            dataFile.seek(indexTable.findDataPosition(object.getLongId()));
            ByteArrayOutputStream bos = serialize(object);



            // new data not fit the block
            if (bos.size() > dataFile.getAvailableSpaceAtCurrentPosition()) {

                // save data at EOF instead
                dataFile.markCurrentBlockDeleted();
                dataFile.seekEOF();
            }


            // update index
            indexTable.save(
                    // key
                    object.getLongId(),
                    // fp will be return from dataFile.writeByteArrayObject(bos)
                    dataFile.writeByteArrayObject(serialize(object))
            );

        } catch (IOException e) {
            // If exception, restore old data
            try {
                dataFile.restoreCurrentBlock(oldChunkSize);
                indexTable.save(object.getLongId(), oldPos);
            } catch (IOException e2) {
                throw new UncheckedIOException(e2);
            }

            throw new UncheckedIOException(e);
        }

    }

    public T delete(long key) {
        T object = get(key);

        // No object found
        if (object == null)
            return null;

        try {
            // find and mark delete
            dataFile.seek(indexTable.findDataPosition(key));
            dataFile.markCurrentBlockDeleted();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return object;
    }

    public T deserialize(ByteArrayInputStream bis) {
        // deserialize bis, return null if ClassNotfoundException (not T.class) or IOException (on bis)

        try {
            // Object deleted
            if (bis == null)
                return null;
            ObjectInputStream ois = new ObjectInputStream(bis);

            return (T) ois.readObject();

        } catch (IOException e) {

            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new ClassCastException(e.getMessage());
        }

    }

    public ByteArrayOutputStream serialize(T object) {

        // Serialize dataObject and put it in bos
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public boolean hasNext() {
        return dataFile.hasNext();
    }

    @Override
    public T next() {

        try {
            T t = deserialize(dataFile.readByteArrayObject());
            return t;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long rewind() {
        try {
            dataFile.seekStart();
            return dataFile.getRaf().getFilePointer();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public long getFilePointer() {
        try {
            return dataFile.getRaf().getFilePointer();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void seek(long pos) {
        try {
            dataFile.getRaf().seek(pos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Set<Long> keySet() {
        return indexTable.keySet();
    }

    public void dumpHEX(String filename) {
        // Create directory if missing
        File file = new File(filename+"_hex_dump.txt");
        if (! file.exists() && ! file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileWriter fileWriter = new FileWriter(filename+"_hex_dump.txt", false);
             PrintWriter printWriter = new PrintWriter(fileWriter);)
        {

            printWriter.println("# Java HEX Dump file \n");
            printWriter.printf("#  Original: %s.*\n\n",filename);

            dataFile.getRaf().seek(64);

            while(dataFile.getRaf().getFilePointer() < dataFile.getRaf().length()) {
                printWriter.println(dumpBlock());
            }

        } catch (IOException e) {

        }
    }

    private String dumpBlock() throws IOException {
        StringBuilder sb = new StringBuilder();
        RandomAccessFile raf = dataFile.getRaf();
        long pos = raf.getFilePointer();

        sb.append("[Block Header]\n");
        int blockCount = raf.readInt();
        sb.append(String.format("%08X+%04X -> %08X %08X ; BlockCount = %08X (%d)\n", pos, 0, blockCount, raf.readInt(), blockCount, blockCount));

        int chunkSize = raf.readInt();
        sb.append(String.format( "%8s+%04X -> %08X %08X ; ChunkSize = %08X (%d) %s\n", "", 8, chunkSize, raf.readInt(), chunkSize, chunkSize, chunkSize==0?"[DELETED]":""));

        sb.append(String.format( "%8s+%04X -> %08X %08X %08X %08X ; Reserved\n", "", 16, raf.readInt(), raf.readInt(), raf.readInt(), raf.readInt()));
        raf.seek(pos);
        T t = deserialize(dataFile.readByteArrayObject());
        sb.append("[Serialized Object]\n");
        raf.seek(pos+32);
        if (chunkSize > 0) {
            sb.append(t.dump()).append("\n");
            sb.append(String.format("%8s+%04X -> %08X %08X %08X %08X ; Object (id=%d) \n", "", 32, raf.readInt(), raf.readInt(), raf.readInt(), raf.readInt(), t.getLongId()));
        } else {
            sb.append(String.format("%8s+%04X -> %08X %08X %08X %08X ; null \n", "", 32, raf.readInt(), raf.readInt(), raf.readInt(), raf.readInt()));
        }
        sb.append(String.format( "%17s.... .... .... ....\n","" ));

        raf.seek(pos+(blockCount*64)+32);

        return sb.toString();
    }
}
