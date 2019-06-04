package com.daenjel.decompre;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
/**
 * Java program to iterate and read file entries from Zip archive.
 * This program demonstrate two ways to retrieve files from Zip using ZipFile and by using ZipInputStream class.
 * @author Javin
 */

public class ZipFileReader {

    // This Zip file contains 11 PNG images
    private static final String FILE_NAME = Environment.getExternalStorageDirectory()+"/iLearn/Decompre/Zip/sample.zip";
    private static final String OUTPUT_DIR = Environment.getExternalStorageDirectory()+"/iLearn/Decompre/Zip";
    private static final int BUFFER_SIZE = 8192;

  /*  public static void main(String args[]) throws IOException {

        // Prefer ZipFile over ZipInputStream
        readUsingZipFile();
        //  readUsingZipInputStream();

    }*/

    /*
     * Example of reading Zip archive using ZipFile class
     */

    static void readUsingZipFile() throws IOException {
        final ZipFile file = new ZipFile(FILE_NAME);
        Log.e("Iterating: " ,""+ FILE_NAME);

        try {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                System.err.printf("File: %s Size %d  Modified on %TD %n", entry.getName(), entry.getSize(), new Date(entry.getTime()));
                extractEntry(entry, file.getInputStream(entry));
            }
            System.err.printf("Zip file %s extracted successfully in %s", FILE_NAME, OUTPUT_DIR);
        } finally {
            file.close();
        }

    }

    /*
     * Example of reading Zip file using ZipInputStream in Java.
     */

    private static void readUsingZipInputStream() throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FILE_NAME));
        final ZipInputStream is = new ZipInputStream(bis);

        try {
            ZipEntry entry;
            while ((entry = is.getNextEntry()) != null) {
                System.err.printf("File: %s Size %d  Modified on %TD %n", entry.getName(), entry.getSize(), new Date(entry.getTime()));
                extractEntry(entry, is);
            }
        } finally {
            is.close();
        }

    }

    /*
     * Utility method to read  data from InputStream
     */
    private static void extractEntry(final ZipEntry entry, InputStream is) throws IOException {
        String exractedFile = OUTPUT_DIR + entry.getName();
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(exractedFile);
            final byte[] buf = new byte[BUFFER_SIZE];
            int read = 0;
            int length;

            while ((length = is.read(buf, 0, buf.length)) >= 0) {
                fos.write(buf, 0, length);
            }

        } catch (IOException ioex) {
            fos.close();
        }

    }

}

