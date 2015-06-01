package com.project.android.nctu.nomorelost;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;


public class LostItemDetailsActivity extends Activity {

    private final String TAG = "LostItemActivity";
    private JSONObject lostItem;
    private TextView textViewMail, textViewContact, textViewDescription;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader imageLoader = ImageLoader.getInstance();
        setContentView(R.layout.activity_lost_item);

        findView();

        Bundle bundle = this.getIntent().getExtras();

        try {
            lostItem = new JSONObject(bundle.getString("lostitem"));
//            Log.d(TAG, "category: " + lostItem.getString("category"));

            JSONObject picture = lostItem.getJSONObject("picture");
            JSONObject picture2 = picture.getJSONObject("picture");
            // JSONObject thumb = picture2.getJSONObject("url");
            String temp = "http://img.hexun.com.tw/2011-06-01/130166523.jpg";

            Bitmap img = convertStringToIcon(temp);
            // item.put("thumb",  img );
            // String st = lostItem.getString("")
            textViewMail.setText(lostItem.getString("mail"));
            textViewContact.setText(lostItem.getString("contact"));
            textViewDescription.setText(lostItem.getString("description"));
            // imageView.setImageBitmap(img);
            // String randomString = String.format("?random=%d", System.currentTimeMillis());
            String imageUri = "http://52.68.136.81:3000/" + picture2.getString("url");
            imageLoader.displayImage(imageUri, imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void findView() {
        textViewMail = (TextView) findViewById(R.id.textView_mail);
        textViewContact = (TextView) findViewById(R.id.textView_contact);
        textViewDescription = (TextView) findViewById(R.id.textView_description);
        imageView = (ImageView) findViewById(R.id.imageView2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lost_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            java.net.URL url = new java.net.URL(st);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(is);
            return mBitmap;

        } catch (Exception e) {
            return null;
        }
    }
}
