package com.project.android.nctu.nomorelost;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


public class UploadLostItem extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_lost_item);

        Spinner SpinnerS = (Spinner)findViewById(R.id.spinner01);

        //設定功能表項目陣列，使用createFromResource()

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.lostitem_category,

                android.R.layout.simple_spinner_item);

        //設定選單

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //設定adapter

        SpinnerS.setAdapter(adapter);
    }
}
