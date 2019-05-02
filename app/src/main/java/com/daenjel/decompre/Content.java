package com.daenjel.decompre;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daenjel.decompre.InternalStorage.InternalStore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Content extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE = 1;
    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath + "/iLearn/Decompre/data/" ;
    private String zipPath = SDPath + "/iLearn/Decompre/Zip/" ;
    private String unzipPath = SDPath + "/iLearn/Decompre/Unzip/" ;

    //final static String TAG = MainActivity.class.getName();

    Button btnUnzip, btnZip,btnInter,btnRead;
    TextView textView;
    StorageReference islandRef;
    FirebaseStorage storageRef;
    File localFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageRef = FirebaseStorage.getInstance();
        btnZip = findViewById(R.id.btnZip);
        btnUnzip = findViewById(R.id.btnUnzip);
        textView = findViewById(R.id.viewer);
        btnInter = findViewById(R.id.btnInter);
        btnRead = findViewById(R.id.btnRead);

        checkPermit();

        File rootPath = new File(Environment.getExternalStorageDirectory(), zipPath);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        localFile = new File(rootPath,"sample.zip");
        final String dataFile = unzipPath + "sample.txt" ;
        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFire();
                /*if (FileHelper.zip(dataPath, zipPath, "sample.zip", chkParent.isChecked())){
                    Toast.makeText(MainActivity.this,"Zip successfully.",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this,"Zip Unsuccessfully.",Toast.LENGTH_LONG).show();
                }*/
            }
        });
        btnUnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.unzip(localFile+"",unzipPath)) {
                    textView.setText("Extraction successfully.");
                    Toast.makeText(Content.this,"Extraction successfully.",Toast.LENGTH_LONG).show();
                    Log.e("LOCATOR","->"+localFile+"....Dumping: ->"+unzipPath);
                }else {
                    textView.setText("Extraction Unsuccessfully.");
                    Toast.makeText(Content.this,"Extraction Unsuccessfully.",Toast.LENGTH_LONG).show();
                    Log.e("LOCATOR","->"+localFile+"....Dumping: ->"+unzipPath);
                }
            }
        });

        //Create dummy files
        FileHelper.saveToFile(dataPath,"Welcome to iLearn Mathematics", "math.txt");
        FileHelper.saveToFile(dataPath,"Welcome to iLearn English", "english.txt");
        FileHelper.saveToFile(dataPath,"Welcome to iLearn Swahili", "swa.txt");

        btnInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InternalStore.class));
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = getApplicationContext();

                    FileInputStream fileInputStream = new FileInputStream(new File(dataFile));

                    String fileData = readFromFileInputStream(fileInputStream);

                    if (fileData.length() > 0) {
                        textView.setText(fileData);
                        //textView.setSelection(fileData.length());
                        Toast.makeText(ctx, "Load saved data complete.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ctx, "Not load any data.", Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException ex) {
                    Log.e("TAG_WRITE_READ_FILE", ex.getMessage(), ex);
                }
            }
        });
    }

    private void downloadFire() {
        islandRef = storageRef.getReference().child("sample.rar");

        /* File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //islandRef = storageRef.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/wasazi-220412.appspot.com/o/sample.txt?alt=media&token=3abdc0c4-5c6b-4f2c-8e42-321a372fd58e").child("sample.txt");


        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                textView.setText("Download successfully.");
                Toast.makeText(Content.this,"Download successfully.",Toast.LENGTH_LONG).show();
                Log.e("LOCATION","->"+localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                textView.setText("Download Failed."+exception.getMessage());
                Toast.makeText(Content.this,"Download Failed!."+exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkPermit() {

        int external = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (external != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_WRITE);
        }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE){
            int resultLength = grantResults.length;
            if (resultLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Content.this,"Permission Granted successfully.",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(Content.this,"Permission Not Granted.",Toast.LENGTH_LONG).show();
            }
        }
    }
}