package com.daenjel.decompre;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.LiveFolders;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encryption extends AppCompatActivity {

    Button encrypt,decrypt;
    EditText editText;
    WebView webView;
    String readPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/iLearn/Decompre/Unzip/READING/COMPREHENSION/ENG2-3-1-001.html";
    String lockPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/iLearn/Locked/";
    File rootPath;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

        webView = findViewById(R.id.web);
        editText = findViewById(R.id.editText);
        encrypt = findViewById(R.id.btnEncrypt);
        decrypt = findViewById(R.id.btnDecrypt);

        rootPath = new File(Environment.getExternalStorageDirectory() + "/iLearn/Encryption");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        Log.w("HTML",readPath);
        webView.loadUrl(readPath);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(true);
        webView.setWebViewClient(new WebViewClient());

        int dataFile = R.drawable.eng002_atlas_;
        final byte [] data = String.valueOf(readPath).getBytes();
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save files
                try {
                    String pwd = editText.getText().toString();
                    Log.e("DATA", " -> "+ Arrays.toString(data));

                    File file = new File(rootPath+ "");
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] yourKey = generateKey(pwd);
                    byte[] fileBytes = encodeFile(yourKey, data);
                    bos.write(fileBytes);
                    bos.flush();
                    bos.close();
                    Log.e("ENCRYPTED", " -> "+ Arrays.toString(fileBytes));

                    File fil = new File(rootPath+ "");
                    BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(fil));
                    byte[] decodedData = decodeFile(yourKey, fileBytes);
                    b.write(decodedData);
                    b.flush();
                    b.close();
                    Log.e("DECODED", " -> "+  Arrays.toString(decodedData));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                lockFiles();
                //decode to use
                try {
                   /* String pwd = editText.getText().toString();
                    Log.e("DATA", " -> "+ Arrays.toString(data));

                    *//*File file = new File(rootPath+ "data_de.txt");
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] yourKey = generateKey(pwd);
                    byte[] fileBytes = decodeFile(yourKey, data);
                    bos.write(fileBytes);
                    bos.flush();
                    bos.close();
                    Log.e("ENCRYPTED", " -> "+ Arrays.toString(fileBytes));

                    byte[] decodedData = decodeFile(yourKey, fileBytes);
                    Log.e("DECODED", " -> "+ new String(decodedData));*//*
                    Context ctx = getApplicationContext();

                    FileInputStream fileInputStream = new FileInputStream(new File(rootPath +"data.txt"));

                    String fileData = readFromFileInputStream(fileInputStream);
                    byte[] decrypt = fileData.getBytes();

                    if (fileData.length() > 0) {
                        Log.e("ENCODED", " -> "+ fileData);
                        byte[] yourKey = generateKey(pwd);
                        byte[] decodedData = decodeFile(yourKey, decrypt);
                        Log.e("DECODED", " -> "+ new String(decodedData));
                        Toast.makeText(ctx, "Load saved data complete.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ctx, "Not load any data.", Toast.LENGTH_SHORT).show();
                    }*/
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void lockFiles() {

       /* FileInputStream in = null;
        try {
            Log.e("DIRE",""+rootPath+"data.txt");
            in = new FileInputStream(new File(rootPath+"data.txt"));
            java.nio.channels.FileLock lock = in.getChannel().lock();
            try {
                Reader reader = new InputStreamReader(in);

            } finally {
                lock.release();
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {/*
            Log.e("DIRE",""+rootPath+"data.txt");
            FileInputStream in = new FileInputStream(new File(rootPath+"data.txt"));*/
            // Creates a random access file stream to read from, and optionally to write to
            FileChannel channel = new RandomAccessFile(new File(rootPath+"data.txt"), "rw").getChannel();

            // Acquire an exclusive lock on this channel's file (blocks until lock can be retrieved)
            FileLock lock = channel.lock();

            // Attempts to acquire an exclusive lock on this channel's file (returns null or throws
            // an exception if the file is already locked.
            /*try {
                lock = channel.tryLock();
            }
            catch (OverlappingFileLockException e) {
                // thrown when an attempt is made to acquire a lock on a a file that overlaps
                // a region already locked by the same JVM or when another thread is already
                // waiting to lock an overlapping region of the same file
                System.err.println("Overlapping File Lock Error: " + e.getMessage());
            }*/

            // release the lock
            lock.release();

            // close the channel
            channel.close();

        }
        catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }

    }

    public static byte[] generateKey(String password) throws Exception {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        return cipher.doFinal(fileData);
    }

    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);


        return cipher.doFinal(fileData);
    }
    private String readFromFileInputStream(FileInputStream fileInputStream) {
        StringBuffer retBuf = new StringBuffer();

        try {
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lineData = bufferedReader.readLine();
                while (lineData != null) {
                    retBuf.append(lineData);
                    lineData = bufferedReader.readLine();
                }
            }
        } catch (IOException ex) {
            Log.e("TAG_WRITE_READ_FILE", ex.getMessage(), ex);
        } finally {
            return retBuf.toString();
        }
    }

}
