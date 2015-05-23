package com.project.android.nctu.nomorelost;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.android.nctu.nomorelost.utils.ApiRequestClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class LostitemsActivity extends AppCompatActivity {

    private final String TAG = "LostitemsActivity";

    public JSONArray lostitems;

    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostitems);

        getLostitemsList();
    }

    private void getLostitemsList() {
        String url = "http://52.68.136.81:3000/api/lostitems";

        ApiRequestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                /** If the response is JSONObject instead of expected JSONArray */
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray lostitemsList) {
                lostitems = lostitemsList;
                Log.e(TAG, "lostitems: " + lostitems);
                for (int i = 0; i < lostitems.length(); i++) {
                    try {
                        JSONObject lostitem = (JSONObject) lostitems.get(i);

                        HashMap<String, Object> item = new HashMap<String, Object>();

                        item.put("mail", lostitem.getString("mail"));
                        item.put("contact", lostitem.getString("contact"));
                        item.put("description", lostitem.getString("description"));

                        Log.e(TAG, "mail: " + lostitem.getString("mail"));
                        Log.e(TAG, "contact: " + lostitem.getString("contact"));
                        Log.e(TAG, "description: " + lostitem.getString("description"));

                        list.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lostitems, menu);
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
