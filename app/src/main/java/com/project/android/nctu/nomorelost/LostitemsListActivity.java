package com.project.android.nctu.nomorelost;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.android.nctu.nomorelost.utils.ApiRequestClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class LostitemsListActivity extends Activity {

    private final String TAG = "LostitemsListActivity";
    private ListView mListView;
    public JSONArray lostitems;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

    private SimpleAdapter adapter;
    private TextView textViewCategory, textViewContact, textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostitems_list_activity);

        findView();
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

                for (int i = 0; i < lostitems.length(); i++) {
                    try {
                        JSONObject lostitem = (JSONObject) lostitems.get(i);

                        HashMap<String, Object> item = new HashMap<String, Object>();

                        item.put("mail", lostitem.getString("mail"));
                        item.put("contact", lostitem.getString("contact"));
                        item.put("description", lostitem.getString("description"));

                        JSONObject category = lostitem.getJSONObject("category");
                        item.put("category", category.getString("name"));

                        item.put("created_at", lostitem.getString("created_at"));

                        list.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setSimpleAdapter();
            }
        });
    }

    private void setSimpleAdapter() {
        adapter = new SimpleAdapter(getApplicationContext(),
                                    list,
                                    R.layout.lostitems_row,
                                    new String[]{"category", "description", "contact"},
                                    new int[]{R.id.lostitem_category, R.id.textView_description, R.id.textView_contact});

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                JSONObject lostitem = (JSONObject) lostitems.get(position);
                Log.e(TAG, "lostitem: " + lostitem);
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LostItemDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("lostitem", lostitem.toString());
                intent.putExtras(bundle);

                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void findView() {
        mListView = (ListView)findViewById(R.id.lostitem_list);
        textViewCategory = (TextView) findViewById(R.id.lostitem_category);
        textViewContact = (TextView) findViewById(R.id.textView_contact);
        textViewDescription = (TextView) findViewById(R.id.textView_description);
    }

    public void back(View view) {
        finish();
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
