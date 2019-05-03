package com.daenjel.decompre;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

        editText = findViewById(R.id.editText);
        encrypt = findViewById(R.id.btnEncrypt);
        decrypt = findViewById(R.id.btnDecrypt);

        final File rootPath = new File(Environment.getExternalStorageDirectory()+"/iLearn/Encryption/");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        String dataFile = "Leo ni Leo hapa iLearn";
        final byte [] data = dataFile.getBytes();
        final String dFile = rootPath + "data.txt" ;
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save files
                try {
                    String pwd = editText.getText().toString();
                    Log.e("DATA", " -> "+ Arrays.toString(data));

                    File file = new File(rootPath+ "data.txt");
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] yourKey = generateKey(pwd);
                    byte[] fileBytes = encodeFile(yourKey, data);
                    bos.write(fileBytes);
                    bos.flush();
                    bos.close();
                    Log.e("ENCRYPTED", " -> "+ Arrays.toString(fileBytes));

                    byte[] decodedData = decodeFile(yourKey, fileBytes);
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
                //decode to use
                try {
                    String pwd = editText.getText().toString();
                    /*Log.e("DATA", " -> "+ Arrays.toString(data));

                    File file = new File(rootPath+ "data_de.txt");
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] yourKey = generateKey(pwd);
                    byte[] fileBytes = decodeFile(yourKey, data);
                    bos.write(fileBytes);
                    bos.flush();
                    bos.close();
                    Log.e("ENCRYPTED", " -> "+ Arrays.toString(fileBytes));

                    byte[] decodedData = decodeFile(yourKey, fileBytes);
                    Log.e("DECODED", " -> "+ new String(decodedData));*/
                    Context ctx = getApplicationContext();

                    FileInputStream fileInputStream = new FileInputStream(new File(rootPath +"data.txt"));

                    String fileData = readFromFileInputStream(fileInputStream);

                    if (fileData.length() > 0) {
                        Log.e("ENCODED", " -> "+ Arrays.toString(fileData.getBytes()));
                        byte[] yourKey = generateKey(pwd);
                        byte[] decodedData = decodeFile(yourKey, fileData.getBytes());
                        Log.e("DECODED", " -> "+ new String(decodedData));
                        Toast.makeText(ctx, "Load saved data complete.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ctx, "Not load any data.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
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
