package com.daenjel.decompre;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE = 1;
    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String zipPath = SDPath + "/iLearn/Decompre/Zip/";
    private String unzipPath = SDPath + "/iLearn/Decompre/Unzip/";
    //private String locker = SDPath + "/iLearn/Locked/";

    //final static String TAG = MainActivity.class.getName();

    Button btnUnzip, btnDownload,btnRead,btnEncrypt;
    TextView textView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    StorageReference islandRef;
    FirebaseStorage storageRef;
    File localFile, rootPath,unLockFile;
    EditText userPassword;
    Context context;
    String dataFile;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;

        storageRef = FirebaseStorage.getInstance();
        btnDownload = findViewById(R.id.btnZip);
        btnUnzip = findViewById(R.id.btnUnzip);
        textView = findViewById(R.id.viewer);
        btnRead = findViewById(R.id.btnRead);
        progressBar = findViewById(R.id.progress);
        btnEncrypt = findViewById(R.id.btnNext);
        userPassword = findViewById(R.id.txt_input);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        checkPermit();

        rootPath = new File(zipPath);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        localFile = new File(rootPath,"sample.zip");
        unLockFile = new File(SDPath+"/iLearn/Decompre/Zip.locked");
        dataFile = unzipPath + "sample.txt" ;

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Encryption.class));
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFire();
            }
        });
        btnUnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockFolder();
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockFolder();
            }
        });
    }
    public void unlockFolder(){
        String password = userPassword.getText().toString();
        File f=new File(String.valueOf(unLockFile));
        if(password.length()>0){
            if(f.isFile()){

                if(isMatched(password)){
                    BackTaskUnlock btunlock=new BackTaskUnlock();
                    btunlock.execute(password,null,null);
                }
                else{
                    MessageAlert.showAlert("Invalid password or folder not locked",context);
                    Log.e("FOLDER NOT LOCKED",f+"");
                    // /storage/emulated/0/iLearn/Decompre/Zip.locked
                }
            }
            else{
                MessageAlert.showAlert("Please select a locked folder to unlock",context);
                Log.e("NOT FOLDER",f+"");
            }
        }
        else{
            MessageAlert.showAlert("Please enter password",context);
        }
    }
    public boolean isMatched(String password){
        boolean mat=false;
        Unlocker locker=new Unlocker(context, unLockFile+"", password);
        byte[] pas=locker.getPassword();
        int passwordRead=locker.byteArrayToInt(pas);
        int passwordInput=locker.byteArrayToInt(password.getBytes());
        if(passwordRead==passwordInput) mat=true;
        return mat;
    }

    public void startUnlock(String password){
        Unlocker unlocker=new Unlocker(context,unLockFile+"",password);
        unlocker.unlock();
        Log.e("LOCKED",unLockFile+"");
    }


    private class BackTaskUnlock extends AsyncTask<String,Void,Void>{
        ProgressDialog pd;
        protected void onPreExecute(){
            super.onPreExecute();
            //show process dialog
            pd = new ProgressDialog(context);
            pd.setTitle("Unlocking the folder");
            pd.setMessage("Please wait.");
            pd.setCancelable(false );
            pd.setIndeterminate(true);
            pd.show();

            Log.e("UNLOCKED","true");
        }
        protected Void doInBackground(String...params){
            try{

                startUnlock(params[0]);

            }catch(Exception e){
                pd.dismiss();   //close the dialog if error occurs

            }
            return null;

        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            //listDirContents(path);//refresh the list
        }


    }
    private void readFile() {
        progressDialog.show();
        try {
            Context ctx = getApplicationContext();

            FileInputStream fileInputStream = new FileInputStream(new File(dataFile));

            String fileData = readFromFileInputStream(fileInputStream);

            if (fileData.length() > 0) {
                textView.setText(fileData);
                //textView.setSelection(fileData.length());
                progressDialog.hide();
                Toast.makeText(ctx, "Load saved data complete.", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.hide();
                Toast.makeText(ctx, "Not load any data.", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException ex) {
            progressDialog.hide();
            textView.setText("(No such file or directory)");
            Log.e("TAG_WRITE_READ_FILE", ex.getMessage(), ex);
        }
    }
    private void downloadFire() {
        progressDialog.show();
       islandRef = storageRef.getReference().child("EnglishG2.zip");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                progressDialog.hide();
                textView.setText("Download successful.");
                Toast.makeText(MainActivity.this,"Download successful.",Toast.LENGTH_LONG).show();
                Log.e("LOCATION","->"+localFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.hide();
                textView.setText("Download Failed."+exception.getMessage());
                Toast.makeText(MainActivity.this,"Download Failed!."+exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //displaying percentage in progress dialog
                progressDialog.setMessage("Loading.."+(int) progress +" %");
            }
        });
    }
    private void extractFiles(){
        //progressDialog.setMessage("Installing..");
        progressBar.setVisibility(View.VISIBLE);
        if (FileHelper.unzip(localFile+"",unzipPath)) {
            textView.setText("Extraction successfully.");
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,"Extraction successfully.",Toast.LENGTH_LONG).show();
            Log.e("LOCATOR","->"+localFile+"....Dumping: ->"+unzipPath);
        }else {
            progressBar.setVisibility(View.GONE);
            textView.setText("Extraction Unsuccessfully.");
            Toast.makeText(MainActivity.this,"Extraction Unsuccessfully.",Toast.LENGTH_LONG).show();
            Log.e("LOCATOR","->"+localFile+"....Dumping: ->"+unzipPath);
        }
    }
    public void lockFolder(){
        String password = userPassword.getText().toString();

        File f = new File(String.valueOf(rootPath));
        if(password.length()>0){

            if(f.isDirectory()){
                BackTaskLock btnLock = new BackTaskLock();
                btnLock.execute(password,null,null);
                Log.e("SRC FOLDER",String.valueOf(rootPath));
                Log.e("SRC FILE",localFile+"");
            }
            else{
                MessageAlert.showAlert("It is not a folder.",context);
            }
        }
        else{
            MessageAlert.showAlert("Please enter password",context);
        }
    }
    private class BackTaskLock extends AsyncTask<String,Void,Void> {
        ProgressDialog pd;
        protected void onPreExecute(){
            super.onPreExecute();
            //show process dialog
            pd = new ProgressDialog(context);
            pd.setTitle("Locking the folder");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
            Log.e("LOCKED","true");
        }
        protected Void doInBackground(String...params){
            try{

                startLock(params[0]);
                Log.e("BACKGROUND","true");

            }catch(Exception e){
                pd.dismiss();   //close the dialog if error occurs
                Log.e("HANDLING BACKGROUND",e.getMessage());
            }
            return null;

        }
        protected void onPostExecute(Void result){
            pd.dismiss();
        }

    }
    public void startLock(String password){
        Locker locker = new Locker(context,String.valueOf(rootPath),password);
        locker.lock();
        Log.e("LOCKING","true");
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void checkPermit() {

        int external = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (external != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_WRITE);
        }
        int network = ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET);
        if (network != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.INTERNET},REQUEST_CODE_WRITE);
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
                Toast.makeText(MainActivity.this,"Permission Granted successfully.",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MainActivity.this,"Permission Not Granted.",Toast.LENGTH_LONG).show();
            }
        }
    }
}