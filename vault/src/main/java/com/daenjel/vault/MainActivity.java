package com.daenjel.vault;

import java.io.File;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
public class MainActivity extends Activity {

    private String path= "";
    private String selectedFile="";
    private Context context;
    EditText userPassword;
    ListView lv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context=this;

        userPassword = findViewById(R.id.txt_input);
        lv = findViewById(R.id.files_list);

    }

    protected void onStart(){
        super.onStart();
        if(lv!=null){
            lv.setSelector(R.drawable.selection_style);
            lv.setOnItemClickListener(new ClickListener());
        }
        path="/mnt";
        listDirContents(path);
    }

    public void onBackPressed(){
        goBack();
    }

    public void goBack(){
        if(path.length()>1){ //up one level of directory structure
            File f=new File(path);
            path=f.getParent();
            listDirContents(path);
        }
        else{
            refreshThumbnails();
            System.exit(0); //exit app

        }
    }


    private void refreshThumbnails(){
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private class ClickListener implements OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //selected item
            ViewGroup vg=(ViewGroup)view;
            String selectedItem = ((TextView) vg.findViewById(R.id.label)).getText().toString();
            path=path+"/"+selectedItem;
            Log.e("SELECTED",""+path);
            //et.setText(path);
            listDirContents(path);
        }


    }


    private void listDirContents(String path){
        if(path!=null){
            try{
                File f=new File(path);
                if(f!=null){
                    if(f.isDirectory()){
                        String[] contents=f.list();
                        if(contents.length>0){
                            //create the data source for the list
                            ListAdapterModel adapter =new ListAdapterModel(this,R.layout.listlayout,R.id.label,contents,path);
                            //supply the data source to the list so that they are ready to display
                            lv.setAdapter(adapter);
                            selectedFile=path;
                        }
                        else
                        {
                            //keep track the parent directory of empty directory
                            path=f.getParent();
                        }
                    }
                    else{
                        //capture the selected file path
                        selectedFile=path;
                        //keep track the parent directory of the selected file
                        path=f.getParent();

                    }
                }
            }catch(Exception e){}
        }

    }

    public void lockFolder(View view){
        String password=userPassword.getText().toString();
        File f=new File(selectedFile);
        if(password.length()>0){

            if(f.isDirectory()){
                BackTaskLock btlock=new BackTaskLock();
                btlock.execute(password,null,null);

            }
            else{
                MessageAlert.showAlert("It is not a folder.",context);
            }
        }
        else{
            MessageAlert.showAlert("Please enter password",context);
        }
    }

    public void startLock(String password){
        Locker locker=new Locker(context,selectedFile,password);
        locker.lock();
    }

    public void unlockFolder(View view){
        String password=userPassword.getText().toString();
        File f=new File(selectedFile);
        if(password.length()>0){

            if(f.isFile()){

                if(isMatched(password)){
                    BackTaskUnlock btunlock=new BackTaskUnlock();
                    btunlock.execute(password,null,null);
                }
                else{
                    MessageAlert.showAlert("Invalid password or folder not locked",context);
                }

            }

            else{
                MessageAlert.showAlert("Please select a locked folder to unlock",context);
            }
        }
        else{
            MessageAlert.showAlert("Please enter password",context);
        }

    }

    public boolean isMatched(String password){
        boolean mat=false;
        Locker locker=new Locker(context, selectedFile, password);
        byte[] pas=locker.getPassword();
        int passwordRead=locker.byteArrayToInt(pas);
        int passwordInput=locker.byteArrayToInt(password.getBytes());
        if(passwordRead==passwordInput) mat=true;
        return mat;
    }

    private class BackTaskLock extends AsyncTask<String,Void,Void>{
        ProgressDialog pd;
        protected void onPreExecute(){
            super.onPreExecute();
            //show process dialog
            pd = new ProgressDialog(context);
            pd.setTitle("Locking the folder");
            pd.setMessage("Please wait.");
            pd.setCancelable(true);
            pd.setIndeterminate(true);
            pd.show();
            Log.e("LOCKED","true");

        }
        protected Void doInBackground(String...params){
            try{

                startLock(params[0]);

            }catch(Exception e){
                pd.dismiss();   //close the dialog if error occurs
            }
            return null;

        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            goBack();
        }


    }


    public void startUnlock(String password){
        Locker locker=new Locker(context,selectedFile,password);
        locker.unlock();
    }


    private class BackTaskUnlock extends AsyncTask<String,Void,Void>{
        ProgressDialog pd;
        protected void onPreExecute(){
            super.onPreExecute();
            //show process dialog
            pd = new ProgressDialog(context);
            pd.setTitle("Unlocking the folder");
            pd.setMessage("Please wait.");
            pd.setCancelable(true);
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
            listDirContents(path);//refresh the list
        }


    }


}
