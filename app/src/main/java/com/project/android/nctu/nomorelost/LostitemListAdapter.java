package com.project.android.nctu.nomorelost;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.project.android.nctu.nomorelost.utils.DownloadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chichunchen on 6/13/15.
 */

public class LostitemListAdapter extends SimpleAdapter {
    ImageView imageView;
    TextView textViewCategory, textViewtitle, textViewdate;
    private Context mContext;
    public LayoutInflater inflater = null;
    ImageLoader imageLoader;

    public LostitemListAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource, String[] from,
                               int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        imageLoader = ImageLoader.getInstance();
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.lostitems_row, null);
        vi.setBackgroundColor(mContext.getResources().getColor((position % 2 == 0) ? R.color.white : R.color.odd_row));

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        // ImageView imageView;
        imageView = (ImageView) vi.findViewById(R.id.imageView);
        textViewCategory = (TextView) vi.findViewById(R.id.lostitem_category);
        textViewdate = (TextView) vi.findViewById(R.id.textView_date);
        textViewtitle = (TextView) vi.findViewById(R.id.textView_title);
        String title = (String) data.get("title");
        String category = (String) data.get("category");
        String date = (String) data.get("created_at");
        textViewdate.setText(date);
        textViewtitle.setText(title);
        textViewCategory.setText(category);
        String url = (String) data.get("thumb");

        imageLoader.displayImage(url, imageView);


        return vi;
    }

}
