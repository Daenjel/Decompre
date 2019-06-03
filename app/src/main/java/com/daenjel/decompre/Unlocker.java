package com.daenjel.decompre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

class Unlocker{

    private String dirpath;
    private String password;
    private Context context;
    Unlocker(Context context,String path,String password){
        this.dirpath=path;
        this.password=password;
        this.context =context;
    }

    public void unlock(){
        boolean isHead=true;

        try{
            File f=new File(dirpath);
            if(f.isFile()){
                FileInputStream fis=new FileInputStream(f);
                File tempfile=new File(context.getFilesDir(),"temp.zip");
                FileOutputStream fos=new FileOutputStream(tempfile);
                FileChannel fc=fis.getChannel();
                Log.e("FOLDER BEING UNLOCKED",f+"");
                int blockSize=1024;
                ByteBuffer bb=ByteBuffer.allocate(blockSize);
                int passwordInput=byteArrayToInt(password.getBytes());
                int nRead;
                while ( (nRead=fc.read( bb )) != -1 )
                {
                    bb.position(0);
                    bb.limit(nRead);
                    //decrypt the head section of the file
                    if(isHead){
                        while ( bb.hasRemaining())
                            fos.write(bb.get()-passwordInput);
                        isHead=false;

                    }
                    else

                        fos.write(bb.array());

                    bb.clear();

                }
                fis.close();
                fos.flush();
                fos.close();

                //Replacing the file content
                String dirParent=f.getParent();
                f.delete();
                File unlockedFile=new File(dirParent+"/unlocked.zip");
                copyFile(tempfile,unlockedFile);
                //extractFile(unlockedFile.getPath(), dirParent);
                Log.e("UNLOCKED FILE",unlockedFile+"");
                unlockedFile.delete();
                //delete the temp file
                tempfile.delete();
                //delete the password
                File filepassword=new File(context.getFilesDir(),getName(dirpath));
                filepassword.delete();

            }

        }catch(IOException e){e.printStackTrace();}

    }

    private void copyFile(File src, File dst) throws IOException{
        FileInputStream fis=new FileInputStream(src);
        FileOutputStream fos=new FileOutputStream(dst);
        FileChannel inChannel =fis.getChannel();
        FileChannel outChannel = fos.getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }catch(IOException e){e.printStackTrace();}
        finally
        {
            if (inChannel != null){
                fis.close();
                inChannel.close();
            }
            if (outChannel != null){
                fos.close();
                outChannel.close();
            }

        }
    }

    public int byteArrayToInt(byte[] password){
        int b=0;
        if(password!=null)
            for(byte y:password){
                b=b+y;
            }
        return b;
    }

    public byte[] getPassword(){
        byte[] password=null;
        try {
            File f=new File(context.getFilesDir(),getName(dirpath));
            if(f.exists()){
                BufferedReader br=new BufferedReader(new FileReader(f));
                password=br.readLine().getBytes();
                br.close();
            }
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return password;

    }

    private String getName(String dirpath){
        return(dirpath.substring(dirpath.lastIndexOf("/")+1));
    }

    private String getParentDir(String dirpath){
        File file=new File(dirpath);
        return(file.getParent());
    }

	public String getPath(String srcpath){

		String path="";
		if(srcpath.endsWith(File.separator)){
			path=srcpath.substring(0,srcpath.length()-1);
			path=path.substring(path.lastIndexOf(File.separator)+1);
		}
		else
			path=srcpath.substring(srcpath.lastIndexOf(File.separator)+1);
		return path;
	}

	public void extractFile(String srcfile, String despath){
    	try {
			ZipFile zf=new ZipFile(srcfile); //create  a zip file object
			if(zf.size()>0){ //read through the zip file
				Enumeration<ZipEntry> entries=(Enumeration<ZipEntry>) zf.entries();
				while(entries.hasMoreElements()){
					ZipEntry entry=entries.nextElement();
					if(!entry.isDirectory() && !entry.getName().endsWith("/")){
						//start extracting the files
						extract(zf.getInputStream(entry),entry.getName(),despath);

					}

				}

			}
			zf.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
    }

	public void extract(InputStream is, String fname, String storeDir){

		FileOutputStream fos;
		//if(fname.endsWith(".locked")) fname=fname.substring(0, fname.lastIndexOf(".locked"));
		File fi=new File(storeDir+File.separator+fname); //output file
		File fparent=new File(fi.getParent());
		fparent.mkdirs();//create parent directories for output files

		try {

			fos=new FileOutputStream(fi);
			int content=0;
			while((content=is.read())!=-1){
				fos.write(content);
			}
			is.close();
			fos.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
