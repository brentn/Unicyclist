package com.unicycle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadRandomImage extends AsyncTask<ImageView, Void, Bitmap> {
	
	ImageView imageView = null;
	

	@Override
	protected Bitmap doInBackground(ImageView... imageViews) {
	    this.imageView = imageViews[0];
	    return download_Image();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
	    imageView.setImageBitmap(result);
	}


	private Bitmap download_Image() {
		Bitmap result=null;
		Random random = new Random();
		try {
	        HttpClient httpclient = new DefaultHttpClient(); 
	        HttpGet httpget = new HttpGet("http://unicyclist.com/forums/showthread.php?t=70696&page=987"); 
	        HttpResponse response = httpclient.execute(httpget); 
	        HttpEntity entity = response.getEntity(); 
	        InputStream stream = entity.getContent(); // Create an InputStream with the response
	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"), 8);
	        List<String> imageURLs = new ArrayList<String>();
	        String line = null;
	        String temp = null;
	        while ((line = reader.readLine()) != null) // Read line by line
	            if (line.contains("attachment.php")) {
	          		temp = line.substring(line.indexOf("attachment.php"));
	            	imageURLs.add("http://unicyclist.com/forums/"+temp.substring(0,temp.indexOf('"')));
	            }
	        stream.close(); // Close the stream
	        String randomUrl = imageURLs.get(random.nextInt(imageURLs.size()));
      	    result = BitmapFactory.decodeStream((InputStream)new URL(randomUrl).getContent());
        } catch (MalformedURLException e) {
      	    e.printStackTrace();
        } catch (IOException e) {
    	    e.printStackTrace();
        }
        return result;
	}

}
