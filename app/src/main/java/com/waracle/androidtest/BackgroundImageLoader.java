package com.waracle.androidtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ed on 20/08/16.
 */


class BackgroundImageLoader extends AsyncTask<String, Void, byte[]> {

    private static final String TAG = BackgroundImageLoader.class.getSimpleName();

    private static final int HTTP_OK = 200;

    Bundle bundy;

    private String url;
    private byte[] is;
    private Handler mHandler;


    public BackgroundImageLoader(Handler mHandler){
        this.mHandler = mHandler;
    }


    @Override
    protected byte[] doInBackground(String... params) {

        this.url = params[0];

        HttpURLConnection connection = null;
        try {
            Log.d(TAG,"connection="+url);
            connection = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            Log.d(TAG,"no connection");
            e.printStackTrace();
        }
        InputStream inputStream = null;

        try {
            // Read data from workstation
            //connection.addRequestProperty("Cache-Control", "only-if-cached");


            int statusCode = connection.getResponseCode();

            //simple check
            if (statusCode == HTTP_OK) {
                // Create an InputStream in order to extract the response object
                inputStream = connection.getInputStream();
            }
            else {
                // Read the error from the workstation
                inputStream = connection.getErrorStream();
            }

            //cached

            // Can you think of a way to make the entire
            // HTTP more efficient using HTTP headers??

            is = StreamUtils.readUnknownFully(inputStream);

        //} catch (FileNotFoundException e) {
            // the resource was not cached
        //    Log.d(TAG, "uncached");
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {

            // Close the input stream if it exists.
            StreamUtils.close(inputStream);
            // Disconnect the connection
            connection.disconnect();
        }

    return is;
    }



    @Override
    protected void onPostExecute(byte[] bM) { //main

        //callback
        Message msg=new Message();
        msg.obj= bM;
        mHandler.sendMessage(msg);
    }

}