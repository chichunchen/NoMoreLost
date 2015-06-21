package com.project.android.nctu.nomorelost.utils;

/**
 * Created by 聖傑 on 2015/6/21.
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadTask extends AsyncTask<String, Void, Boolean> {
    ImageView v;
    String url;
    Bitmap bm;

    public DownloadTask(ImageView v) {
        this.v = v;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        url = params[0];
        bm = loadBitmap(url);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
       // super.onPostExecute(result);
        v.setImageBitmap(bm);
    }

    public static Bitmap loadBitmap(String url) {
        try {
            URL newurl = new URL(url);
            Bitmap b = BitmapFactory.decodeStream(newurl.openConnection()
                    .getInputStream());
            return b;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
           // e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
           // e.printStackTrace();
        }
        return null;
    }
}