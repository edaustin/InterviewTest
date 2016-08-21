package com.waracle.androidtest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static android.R.id.list;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //https://developer.android.com/reference/android/net/http/HttpResponseCache.html
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //https://developer.android.com/reference/android/net/http/HttpResponseCache.html
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment is responsible for loading in some JSON and
     * then displaying a list of cakes with images.
     * Fix any crashes
     * Improve any performance issues
     * Use good coding practices to make code more secure
     */
    public static class PlaceholderFragment extends ListFragment {

        private static final String TAG = PlaceholderFragment.class.getSimpleName();

        private ListView mListView;
        private MyAdapter mAdapter;

        public PlaceholderFragment() { /**/ }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mListView = (ListView) rootView.findViewById(list);
            Log.d(TAG,"ml="+mListView);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Create and set the list adapter.

            Handler handy = new Handler() {
                @Override public void handleMessage(Message msg) {

                    mAdapter = new MyAdapter();

                    mAdapter.setItems((JSONArray) msg.obj);
                    mListView.setAdapter(mAdapter);

                }
            };

            //pass handler in for callback, URL for background
            new BackgroundItemLoader(handy).execute(JSON_URL);
        }


        /**
         * Returns the charset specified in the Content-Type of this header,
         * or the HTTP default (ISO-8859-1) if none can be found.
         */
        public static String parseCharset(String contentType) {
            if (contentType != null) {
                String[] params = contentType.split(",");
                for (int i = 1; i < params.length; i++) {
                    String[] pair = params[i].trim().split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("charset")) {
                            return pair[1];
                        }
                    }
                }
            }
            return "UTF-8";
        }

        private class MyAdapter extends BaseAdapter {

            // Can you think of a better way to represent these items???
            private JSONArray mItems;

            public MyAdapter() {
                this(new JSONArray());
            }

            public MyAdapter(JSONArray items) {
                mItems = items;
            }

            @Override
            public int getCount() {
                return mItems.length();
            }

            @Override
            public Object getItem(int position) {
                try {
                    return mItems.getJSONObject(position);
                } catch (JSONException e) {
                    Log.e("", e.getMessage());
                }
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @SuppressLint("ViewHolder")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View root = inflater.inflate(R.layout.list_item_layout, parent, false);
                if (root != null) {
                    final TextView title = (TextView) root.findViewById(R.id.title);
                    final TextView desc = (TextView) root.findViewById(R.id.desc);
                    final ImageView image = (ImageView) root.findViewById(R.id.image);
                    try {


                        final JSONObject object = (JSONObject) getItem(position);
                        title.setText(object.getString("title"));
                        desc.setText(object.getString("desc"));

                        final String sImage = object.getString("image");

                        new BackgroundImageLoader(new Handler() {
                            @Override public void handleMessage(Message msg) {
                                setImageView(image, convertToBitmap((byte[])msg.obj));
                            }}).execute(sImage);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return root;
            }

            public void setItems(JSONArray items) {
                mItems = items;
            }


        }
    }


    private static Bitmap convertToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static void setImageView(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
