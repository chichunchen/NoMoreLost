package com.project.android.nctu.nomorelost;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.project.android.nctu.nomorelost.utils.DownloadTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chichunchen on 6/13/15.
 */
class ViewHolder {

    ImageView imageView;
    TextView textViewCategory;
    TextView textViewtitle;

}


class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

    static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (loadedImage != null) {
            ImageView imageView = (ImageView) view;

            boolean firstDisplay = !displayedImages.contains(imageUri);
            if (firstDisplay) {

                FadeInBitmapDisplayer.animate(imageView, 200);
                displayedImages.add(imageUri);
            }
        }
    }
}

public class LostitemListAdapter extends SimpleAdapter {

    public DisplayImageOptions getSimpleOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)

                .build();
        return options;
    }

    ImageView imageView;
    TextView textViewCategory,textViewtitle;
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
        DisplayImageOptions options = getSimpleOptions();
        AnimateFirstDisplayListener listener ;
        listener = new  AnimateFirstDisplayListener();
       // View vi = convertView;
        ViewHolder holder = null;
        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)){
            convertView = inflater.inflate(R.layout.lostitems_row, null,false);
            holder =  new ViewHolder();
           // holder =
            //convertView.setTag(holder);

//        vi.setBackgroundColor(mContext.getResources().getColor((position % 2 == 0) ? R.color.white : R.color.odd_row));


     // ImageView imageView;
     // imageView = (ImageView) vi.findViewById(R.id.imageView);
      holder.imageView =      (ImageView) convertView.findViewById(R.id.imageView);
     // textViewCategory = (TextView) vi.findViewById(R.id.lostitem_category);
      holder.textViewCategory = (TextView) convertView.findViewById(R.id.lostitem_category);
      //textViewContact = (TextView) findViewById(R.id.textView_contact);
     // textViewtitle = (TextView) vi.findViewById(R.id.textView_title);
       holder.textViewtitle = (TextView) convertView.findViewById(R.id.textView_title);


       convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();

        }
        String title = (String)data.get("title");
        String category = (String)data.get("category");
        holder.textViewtitle.setText(title);
        holder.textViewCategory.setText(category);
        String url = (String)data.get("thumb");

        imageLoader.displayImage(url,holder.imageView, options,listener);
      //  imageLoader.
        holder.imageView.setTag(url);



        return convertView;
    }

}
