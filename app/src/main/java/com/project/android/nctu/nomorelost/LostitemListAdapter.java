package com.project.android.nctu.nomorelost;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by chichunchen on 6/13/15.
 */
public class LostitemListAdapter extends SimpleAdapter {

    private Context mContext;

    public LostitemListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundColor(mContext.getResources().getColor((position % 2 == 0) ? R.color.white : R.color.odd_row));

        return view;
    }
}
