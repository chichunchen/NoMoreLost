package com.project.android.nctu.nomorelost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;


public class LostItemDetailsActivity extends Activity {

    private final String TAG = "LostItemDetailsActivity";
    Bundle bundle;
    private JSONObject lostItem;

    private ProgressDialog progress;
    private TextView textViewMail, textViewContact, textViewDescription;
    private ImageView imageView;
    JSONObject picture;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);

        bundle = this.getIntent().getExtras();
        findView();

        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... param) {
            getData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(LostItemDetailsActivity.this, "擷取檔案", "下載圖片檔案，請稍候...", true);

            try {
                lostItem = new JSONObject(bundle.getString("lostitem"));
                textViewMail.setText(lostItem.getString("mail"));
                textViewContact.setText(lostItem.getString("contact"));
                textViewDescription.setText(lostItem.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            try {
                String imageUri = "http://52.68.136.81:3000/" + picture.getString("url");
                imageLoader.displayImage(imageUri, imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // All work is done.
            progress.dismiss();
        }
    }

    private void getData() {
        try {
            lostItem = new JSONObject(bundle.getString("lostitem"));
            // picture = lostItem.getJSONObject("picture").getJSONObject("picture");
            picture = lostItem.getJSONObject("picture").getJSONObject("picture").getJSONObject("medium");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findView() {
        imageLoader = ImageLoader.getInstance();
        textViewMail = (TextView) findViewById(R.id.textView_mail);
        textViewContact = (TextView) findViewById(R.id.textView_contact);
        textViewDescription = (TextView) findViewById(R.id.textView_description);
        imageView = (ImageView) findViewById(R.id.lostitem_image);
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

    public void call(View view) {
        try {
            String number = textViewContact.getText().toString();
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse("tel:" + number));
            startActivity(phoneIntent);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),
                    "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
        }
    }
}
