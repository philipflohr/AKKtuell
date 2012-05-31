package org.akk.akktuell.toolkit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.view.Display;

public class Tools {

	private Activity activity = null;
	private static Tools singleton = null;

	public static Tools getInstance(Activity activity) {
		if (singleton == null) {
			singleton = new Tools(activity);
		}

		return singleton;
	}

	private Tools(Activity activity) {
		this.activity = activity;
	}

	public boolean isTablet() {
		if (android.os.Build.VERSION.SDK_INT >= 11) { // honeycomb
			// test screen size, use reflection because isLayoutSizeAtLeast is
			// only available since 11
			Configuration con = activity.getResources().getConfiguration();
			try {
				Method mIsLayoutSizeAtLeast = con.getClass().getMethod(
						"isLayoutSizeAtLeast", int.class);
				boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con,
						0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public boolean isInLandscapeMode() {
		Display display = activity.getWindowManager().getDefaultDisplay();
    	return display.getWidth() > display.getHeight() ? true : false;
	}
	
	
	public File getAndStoreEventPicture(String picSource, String relativeStorePath, Context ctx) {
		File dir = new File("/data/data/org.akk.akktuell/files");
		String imgFilePath = dir.getAbsolutePath();
		imgFilePath += "/" + relativeStorePath;
		File imgFile = new File(imgFilePath);
	    if(imgFile.exists())
	    {
	        return imgFile;
	    } else {
	    	try {
		    	String FILENAME = relativeStorePath;
		    	

		    	//Get file from net
		    	 
		           if(dir.exists()==false) {
		                dir.mkdirs();
		           }

		           URL url = new URL(picSource + relativeStorePath); //you can write here any link

		           long startTime = System.currentTimeMillis();
		           Log.d("DownloadManager", "download begining");
		           Log.d("DownloadManager", "download url:" + url);
		           Log.d("DownloadManager", "downloaded file name:" + FILENAME);

		           /* Open a connection to that URL. */
		           URLConnection ucon = url.openConnection();

		           /*
		            * Define InputStreams to read from the URLConnection.
		            */
		           InputStream is = ucon.getInputStream();
		           BufferedInputStream bis = new BufferedInputStream(is);

		           /*
		            * Read bytes to the Buffer until there is nothing more to read(-1).
		            */
		           ByteArrayBuffer baf = new ByteArrayBuffer(5000);
		           int current = 0;
		           while ((current = bis.read()) != -1) {
		              baf.append((byte) current);
		           }
	
		    	FileOutputStream fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
		    	fos.write(baf.toByteArray());
		    	fos.close();
		    	return getAndStoreEventPicture(picSource, relativeStorePath, ctx);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return null;
	    	}
	    }
	}
	
	public static String getTimeString(GregorianCalendar date) {
		return new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(date.getTime());
	}
	
}
