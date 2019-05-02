package com.daenjel.decompre;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath + "/instinctcoder/zipunzip/data/" ;
    private String zipPath = SDPath + "/instinctcoder/zipunzip/zip/" ;
    private String unzipPath = SDPath + "/instinctcoder/zipunzip/unzip/" ;

    //final static String TAG = MainActivity.class.getName();

    Button btnUnzip, btnZip,btnInter,btnExter;
    CheckBox chkParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnZip = findViewById(R.id.btnZip);
        chkParent = findViewById(R.id.chkParent);
        btnUnzip = findViewById(R.id.btnUnzip);
        btnInter = findViewById(R.id.btnInter);
        btnExter = findViewById(R.id.btnExter);

        checkPermit();
        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.zip(dataPath, zipPath, "dummy.zip", chkParent.isChecked())){
                    Toast.makeText(MainActivity.this,"Zip successfully.",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this,"Zip UnSuccessful.",Toast.LENGTH_LONG).show();
                }
            }
        });
        btnUnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.unzip(zipPath + "dummy.zip",unzipPath)) {
                    Toast.makeText(MainActivity.this,"Unzip successfully.",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this,"UnZip UnSuccessful.",Toast.LENGTH_LONG).show();
                }

            }
        });

        //Create dummy files
        FileHelper.saveToFile(dataPath,"This is dummy data 01", "Dummy1.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 02", "Dummy2.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 03", "Dummy3.txt");

        btnInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),InternalStore.class));
            }
        });
        btnExter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ExternalStore.class));
            }
        });

    }

    private void checkPermit() {

        int External = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (External != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}