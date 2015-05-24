package com.project.android.nctu.nomorelost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.android.nctu.nomorelost.utils.ApiRequestClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LostItemActivity extends AppCompatActivity {

    private final String TAG = "LostItemActivity";
    private HashMap<String, Object> LostItem = new HashMap<String, Object>();
    private String lostItemId;
    private TextView textViewMail, textViewContact, textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);

        Intent intent = getIntent();
        lostItemId = intent.getExtras().getString("id");

        findView();
        getLostitemList();
    }

    private void getLostitemList() {
        String url = "http://52.68.136.81:3000/api/lostitems/" + lostItemId;
        Log.d(TAG, url);

        ApiRequestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject lostitem = response;

                    LostItem.put("mail", lostitem.getString("mail"));
                    LostItem.put("contact", lostitem.getString("contact"));
                    LostItem.put("description", lostitem.getString("description"));

                    Log.d(TAG, "mail: " + LostItem.get("mail").toString());
                    Log.d(TAG, "contact: " + LostItem.get("contact").toString());
                    Log.d(TAG, "description: " + LostItem.get("description").toString());

                    textViewMail.setText(LostItem.get("mail").toString());
                    textViewContact.setText(LostItem.get("contact").toString());
                    textViewDescription.setText(LostItem.get("description").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findView() {
        textViewMail = (TextView) findViewById(R.id.textView_mail);
        textViewContact = (TextView) findViewById(R.id.textView_contact);
        textViewDescription = (TextView) findViewById(R.id.textView_description);
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
}
