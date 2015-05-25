package com.project.android.nctu.nomorelost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void lostItems(View view) {
        Intent intent = new Intent(this, LostitemsListActivity.class);
        startActivity(intent);
    }

    public void uploadLostItem(View view) {
        Intent intent = new Intent(this, UploadLostItem.class);
        startActivity(intent);
    }

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
