package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;

import java.io.*;
import java.util.Iterator;

/**
 * Read and Write Serialized Entity (ByteArray) using RandomAccessFile
 * <br><br>
 * <p>File structure
 * <code><pre>
 * JAVA .DAT A.FI LE.. < File Header 64 bytes
 * RRRR RRRR RRRR RRRR   R = Reserved for future use
 * RRRR RRRR RRRR RRRR
 * RRRR RRRR RRRR RRRR
 * CCCC RRRR BBBB RRRR < Block Header 16 bytes
 * RRRR RRRR RRRR RRRR
 * DDDD DDDD .... DDDD < Data chunk C bytes
 * .... .... .... ....   int C = Chunk size in byte
 * .... .... .... ....   int B = Block count, multiple of 64 bytes,
 * DDDD DDDD .... DDDD           number of 64 byte-block that chunk occupied
 * DDDD DDDD .... DDDD   R = Reserved for future use
 * </pre></code>
 * </p>
 * <p>
 * DataFile does not see the data but only ByteArray of serialized data.
 * Assumed the file pointer was moved to the correct position before read/write data
 * </p>
 * @see RandomAccessFile
 */
public class DataFile implements Closeable, Iterator {

    private static final int blockSize = 64;
    private static final String fileSignature = "JAVA DATA FILE";
    private String filename;
    private RandomAccessFile raf;

    public DataFile(String filename) throws IOException {
        this.filename = filename+".data";

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

    public int getBlockCount() throws IOException {
        long pos = raf.getFilePointer();
        int blockCount = raf.readInt();
        raf.seek(pos);

        return blockCount;
    }

    public int getChunkSize() throws IOException {
        long pos = raf.getFilePointer();
        raf.skipBytes(8);
        int chunkSize = raf.readInt();
        raf.seek(pos);

        return chunkSize;
    }

    public int getAvailableSpaceAtCurrentPosition() throws IOException {
        return blockSize * getBlockCount();
    }

    public void markCurrentBlockDeleted() throws IOException {
        long pos = raf.getFilePointer();

        // retain block count (4 bytes) and reserved info (4 bytes)
        raf.skipBytes(8);

        // set chunk size to 0 = deleted
        raf.writeInt(0);

        raf.seek(pos);
    }

    public void restoreCurrentBlock(int chunkSize) throws IOException {
        long pos = raf.getFilePointer();

        // retain block count (4 bytes) and reserved info (4 bytes)
        raf.skipBytes(8);

        // put back the chunk size to restore
        raf.writeInt(chunkSize);

        raf.seek(pos);
    }

     public long writeByteArrayObject(ByteArrayOutputStream bos) throws IOException {
        if (bos == null)
            return 0;

        // save fp
        long pos = raf.getFilePointer();

        int chunkSize = bos.size();

        // how many block (blockSize = 64 bytes) is needed to store this chunks
        // Round up
        int blockCount = (chunkSize + blockSize - 1) / blockSize;


        // save block header
        raf.writeInt(blockCount);

        // Reserved
         raf.write(new byte[4]);

        // save chunk size
        raf.writeInt(chunkSize);

        // Reserved
        raf.write(new byte[4]);

        // reserved
        raf.write(new byte[16]);

        // write data
        raf.write(bos.toByteArray());

        // pad to fill the block
        raf.write(new byte[blockCount*blockSize - chunkSize]);

        return pos;
    }

    public ByteArrayInputStream readByteArrayObject() throws IOException {

        // if EOF, it will throw EOF exception (IOException)
        int blockCount = raf.readInt();

        // Reserved
        raf.skipBytes(4);

        int chunkSize;

        // data was deleted
        if ((chunkSize=raf.readInt()) == 0)
            return null;

        // Reserved
        raf.skipBytes(4);

        // Reserved
        raf.skipBytes(16);

        byte[] buffer = new byte[chunkSize];

        // read chunk of data from file to buffer
        raf.read(buffer);

        // move across padded area
        raf.skipBytes(blockCount*blockSize - chunkSize);

        // convert into bis, and return the bis
        return new ByteArrayInputStream(buffer);
    }

    // RandomAccessFile
    // Closable : to be used with try-with-resources, or finally block
    @Override
    public void close() throws IOException {
        raf.close();
    }

    public RandomAccessFile getRaf() {
        return raf;
    }

    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    public void seekStart() throws IOException {
        if (raf.length() < 64)
            throw new IOException("file corrupted");

        raf.seek(64);
    }

    public void seekEOF() throws IOException {
        raf.seek(raf.length());
    }

    @Override
    public boolean hasNext() {
        try {
            return raf.getFilePointer() < raf.length();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ByteArrayInputStream  next() {
        try {
            return readByteArrayObject();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // TODO: rewind fp before throw IOException
}
