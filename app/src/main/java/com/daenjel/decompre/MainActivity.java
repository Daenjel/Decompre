package com.daenjel.decompre;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath + "/instinctcoder/zipunzip/data/" ;
    private String zipPath = SDPath + "/instinctcoder/zipunzip/zip/" ;
    private String unzipPath = SDPath + "/instinctcoder/zipunzip/unzip/" ;

    //final static String TAG = MainActivity.class.getName();

    Button btnUnzip, btnZip;
    CheckBox chkParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUnzip = findViewById(R.id.btnUnzip);
        btnUnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.unzip(zipPath + "dummy.zip",unzipPath)) {
                    Toast.makeText(MainActivity.this,"Unzip successfully.",Toast.LENGTH_LONG).show();
                }


            }
        });


        chkParent = findViewById(R.id.chkParent);

        btnZip = findViewById(R.id.btnZip);
        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.zip(dataPath, zipPath, "dummy.zip", chkParent.isChecked())){
                    Toast.makeText(MainActivity.this,"Zip successfully.",Toast.LENGTH_LONG).show();
                }
            }
        });


        //Create dummy files
        FileHelper.saveToFile(dataPath,"This is dummy data 01", "Dummy1.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 02", "Dummy2.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 03", "Dummy3.txt");



    }


}