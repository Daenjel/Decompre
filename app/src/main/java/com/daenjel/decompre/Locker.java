package com.daenjel.decompre;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

class Locker {

	private String dirpath;
	private String password;
	private Context context;
	Locker(Context context, String path, String password){
		this.dirpath=path;
		this.password=password;
		this.context =context;
	}

	public void lock(){
				boolean isHead=true;
				//doCompression(dirpath);
				try{
					File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/iLearn/Decompre/Zip","sample.zip");
					if(f.exists()) {

						FileInputStream fis = new FileInputStream(f);
						Log.e("BEING LOCKED FILE", f + "");
						File tempfile = new File(context.getFilesDir(), "temp.temp");
						FileOutputStream fos = new FileOutputStream(tempfile);
						FileChannel fc = fis.getChannel();
						int passwordInt = byteArrayToInt(password.getBytes());
						int nRead;
						int blockSize = 1024; //encrypt the first 1kb of the package
						ByteBuffer bb = ByteBuffer.allocate(blockSize);

						while ((nRead = fc.read(bb)) != -1) {
							bb.position(0);
							bb.limit(nRead);
							//encrypt only the head section of the file
							if (isHead) {
								while (bb.hasRemaining())
									fos.write(bb.get() + passwordInt);
								isHead = false;

							} else {
								fos.write(bb.array());
							}
							bb.clear();

						}

						fis.close();
						fos.flush();
						fos.close();
						//replacing the file content
						f.delete();
						File file = new File(dirpath);
						FileUtils.deleteDirectory(file);
						//FileUtils.deleteQuietly(file);
						File lockedFile = new File(getParentDir(dirpath) + File.separator + getName(dirpath) + ".locked");
						Log.e("LOCKED FILE", lockedFile + "");
						copyFile(tempfile, lockedFile);
						//delete the temp file
						tempfile.delete();
						//save the password
						savePassword(password);

					}else {
						Log.e("NO SUCH FILE",f+"");
					}

			
				}catch(IOException e){
					e.printStackTrace();
				}
		
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

	private void savePassword(String password){
		try {
			File f=new File(context.getFilesDir(),getName(dirpath)+".locked");	
			BufferedWriter bw=new BufferedWriter(new FileWriter(f));
			bw.write(password);
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String getName(String dirpath){
		return(dirpath.substring(dirpath.lastIndexOf("/")+1));
	}

	private String getParentDir(String dirpath){
		File file=new File(dirpath);
		return(file.getParent());
	}
}
