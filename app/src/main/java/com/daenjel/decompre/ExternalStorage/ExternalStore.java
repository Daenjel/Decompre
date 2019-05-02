package com.daenjel.decompre.ExternalStorage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daenjel.decompre.R;

import java.io.File;
import java.io.FileWriter;

import static com.daenjel.decompre.ExternalStorage.ExternalStorageUtil.getPrivateExternalStorageBaseDir;

public class ExternalStore extends AppCompatActivity {

    private static final String LOG_TAG_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_store);

        setTitle("Android External Storage.");

        final EditText emailEditor = (EditText) findViewById(R.id.external_storage_editor_email);

        // Save email to a public external storage folder.
        Button savePublicExternalStorageButton = (Button) findViewById(R.id.external_storage_button_save_public);
        savePublicExternalStorageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ExternalStorageUtil.isExternalStorageMounted()) {

                        // Check whether this app has write external storage permission or not.
                        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(ExternalStore.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        // If do not grant write external storage permission.
                        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            // Request user to grant write external storage permission.
                            ActivityCompat.requestPermissions(ExternalStore.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                        } else {

                            // Save email_public.txt file to /storage/emulated/0/DCIM folder
                            String publicDcimDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DCIM);

                            File newFile = new File(publicDcimDirPath, "email_public.txt");

                            FileWriter fw = new FileWriter(newFile);

                            fw.write(emailEditor.getText().toString());

                            fw.flush();

                            fw.close();

                            Toast.makeText(getApplicationContext(), "Save to public external storage success. File Path " + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception ex) {
                    Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.getMessage(), ex);

                    Toast.makeText(getApplicationContext(), "Save to public external storage failed. Error message is " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Save email to a app private external storage folder.
        Button savePrivateExternalStorageButton = (Button) findViewById(R.id.external_storage_button_save_private);
        savePrivateExternalStorageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ExternalStorageUtil.isExternalStorageMounted()) {

                        // Check whether this app has write external storage permission or not.
                        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(ExternalStore.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        // If do not grant write external storage permission.
                        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            // Request user to grant write external storage permission.
                            ActivityCompat.requestPermissions(ExternalStore.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                        } else {

                            // Save email_private.txt file to /storage/emulated/0/Android/data/com.dev2qa.example/files folder
                            String privateDirPath = getPrivateExternalStorageBaseDir(getApplicationContext(), null);

                            File newFile = new File(privateDirPath, "email_private.txt");

                            FileWriter fw = new FileWriter(newFile);

                            fw.write(emailEditor.getText().toString());

                            fw.flush();

                            fw.close();

                            Toast.makeText(getApplicationContext(), "Save to private external storage success. File Path " + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception ex) {
                    Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.getMessage(), ex);

                    Toast.makeText(getApplicationContext(), "Save to private external storage failed. Error message is " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Save email to a app private cached external storage folder.
        Button savePrivateCachedExternalStorageButton = (Button) findViewById(R.id.external_storage_button_save_private_cache);
        savePrivateCachedExternalStorageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ExternalStorageUtil.isExternalStorageMounted()) {

                        // Check whether this app has write external storage permission or not.
                        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(ExternalStore.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        // If do not grant write external storage permission.
                        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            // Request user to grant write external storage permission.
                            ActivityCompat.requestPermissions(ExternalStore.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                        } else {

                            // Save email_private_cache.txt file to /storage/emulated/0/Android/data/com.dev2qa.example/cache folder
                            String privateDirPath = ExternalStorageUtil.getPrivateCacheExternalStorageBaseDir(getApplicationContext());

                            File newFile = new File(privateDirPath, "email_private_cache.txt");

                            FileWriter fw = new FileWriter(newFile);

                            fw.write(emailEditor.getText().toString());

                            fw.flush();

                            fw.close();

                            Toast.makeText(getApplicationContext(), "Save to private external storage success. File Path " + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception ex) {
                    Log.e(LOG_TAG_EXTERNAL_STORAGE, ex.getMessage(), ex);

                    Toast.makeText(getApplicationContext(), "Save to private external storage failed. Error message is " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Display private and public external storage directory in android monitor logcat console.
        Button displayExternalStoragePathButton = (Button) findViewById(R.id.external_storage_button_display_path);
        displayExternalStoragePathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Because create custom directory in public external storage folder need write permission
                // So we should grant the permission to the app first.

                // Check whether this app has write external storage permission or not.
                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(ExternalStore.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                // If do not grant write external storage permission.
                if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    // Request user to grant write external storage permission.
                    ActivityCompat.requestPermissions(ExternalStore.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                } else {
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "Below are public external storage folder path.");

                    // Use Environment class to get public external storage directory.
                    File publicDir = Environment.getExternalStorageDirectory();
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "Environment.getExternalStorageDirectory() : " + publicDir.getAbsolutePath());

                    // Because at the beginning of this method, we had grant write external storage permission
                    // So we can create this directory here.
                    File customPublicDir = new File(publicDir, "Custom");
                    customPublicDir.mkdirs();
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "Create custom dir : " + customPublicDir.getAbsolutePath());

                    File musicPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) : " + musicPublicDir.getAbsolutePath());

                    File dcimPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) : " + dcimPublicDir.getAbsolutePath());

                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "**********************************************************************");

                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "Below are android app private external storage folder path.");

                    // Use Context class to get app private external storage directory

                    Context context = getApplicationContext();

                    File privateDir = context.getExternalFilesDir(null);
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "context.getExternalFilesDir(null) : " + privateDir.getAbsolutePath());

                    File musicPrivateDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) : " + musicPrivateDir.getAbsolutePath());

                    File dcimPrivateDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "context.getExternalFilesDir(Environment.DIRECTORY_DCIM) : " + dcimPrivateDir.getAbsolutePath());

                    File customPrivateDir = context.getExternalFilesDir("Custom");
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "context.getExternalFilesDir(\"Custom\") : " + customPrivateDir.getAbsolutePath());

                    File cachedPrivateDir = context.getExternalCacheDir();
                    Log.d(LOG_TAG_EXTERNAL_STORAGE, "context.getExternalCacheDir() : " + cachedPrivateDir.getAbsolutePath());

                    Toast.makeText(context, "Please see the output in android monitor logcat console.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    // This method is invoked after user click buttons in permission grant popup dialog.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
