package com.project.android.nctu.nomorelost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class LostItemActivity extends AppCompatActivity {

    private final String TAG = "LostItemActivity";
    private JSONObject lostItem;
    private TextView textViewMail, textViewContact, textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);

        findView();

        Bundle bundle = this.getIntent().getExtras();

        try {
            lostItem = new JSONObject(bundle.getString("lostitem"));
            Log.d(TAG, "detail: " + lostItem);

            textViewMail.setText(lostItem.getString("mail"));
            textViewContact.setText(lostItem.getString("contact"));
            textViewDescription.setText(lostItem.getString("description"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
