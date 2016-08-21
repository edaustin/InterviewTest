package com.waracle.androidtest;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.waracle.androidtest.MainActivity.PlaceholderFragment.parseCharset;

/**
 * Created by ed on 20/08/16.
 */


class BackgroundItemLoader extends AsyncTask<String,JSONArray,JSONArray> {

    private static final String TAG = BackgroundItemLoader.class.getSimpleName();

    private URL url;
    private HttpURLConnection urlConnection;
    private JSONArray jsa;
    private Handler mHandler;

    public BackgroundItemLoader(Handler mHandler) {
        this.mHandler = mHandler;
    }


    @Override
    protected JSONArray doInBackground(String... params) {

            InputStream in = null;

        try {

            url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());

            // Can you think of a way to improve the performance of loading data
            // using HTTP headers???

            // Also, Do you trust any utils thrown your way????

            byte[] bytes = new byte[0];

            bytes = StreamUtils.readUnknownFully(in);

            // Read in charset of HTTP content.
            String charset = parseCharset(urlConnection.getRequestProperty("Content-Type"));
            String jsonText = null;

            // Convert byte array to appropriate encoded string.
            jsonText = new String(bytes, charset);

            jsa  = new JSONArray(jsonText);

        } catch (MalformedURLException e) {
            Log.d(TAG,"err");
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();

        } finally {
            urlConnection.disconnect();
        }

        return jsa;

    }

    @Override
    protected void onPostExecute(JSONArray jsonText) { //main

        //callback
        Message msg=new Message();
        msg.obj= jsonText;
        mHandler.sendMessage(msg);
    }

}