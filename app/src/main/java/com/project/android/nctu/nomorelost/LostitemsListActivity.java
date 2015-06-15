package com.project.android.nctu.nomorelost;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.project.android.nctu.nomorelost.utils.ApiRequestClient;
import com.project.android.nctu.nomorelost.utils.ToolsHelper;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import android.widget.SimpleAdapter.ViewBinder;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LostitemsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private final String TAG = "LostitemsListActivity";
    private ListView mListView;
    public JSONArray lostitems;
    private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

    private boolean isInitialized = false;
    private FragmentManager mFragmentManager;
    private DialogFragment mMenuDialogFragment;
    private TextView mTitle;
    private SearchView mSearch;
    private ImageView mMenu, thumbImageView;
    private LostitemListAdapter adapter;
    private TextView textViewCategory, textViewContact, textViewDescription;
    private ProgressDialog progress;
    int setting = 0;
    int pos[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostitems_list_activity);

        mFragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();


        findView();
        getLostitemsList();
    }

    private List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.ic_close_blue_36dp);
        menuObjects.add(close);

        MenuObject sport = new MenuObject("生活用品");
        sport.setResource(R.drawable.ic_umbrella2_blue);
        menuObjects.add(sport);

        MenuObject shirt = new MenuObject("衣物配件");
        shirt.setResource(R.drawable.ic_sport_blue);
        menuObjects.add(shirt);

        MenuObject money = new MenuObject("現金證件");
        money.setResource(R.drawable.ic_money_blue);
        menuObjects.add(money);

        MenuObject stationery = new MenuObject("圖書文具");
        stationery.setResource(R.drawable.ic_pen_blue);
        menuObjects.add(stationery);


        MenuObject threec = new MenuObject("３Ｃ周邊");
        threec.setResource(R.drawable.ic_usb2_blue);
        menuObjects.add(threec);

        MenuObject other = new MenuObject("其他");
        other.setResource(R.drawable.ic_else_blue);
        menuObjects.add(other);

        MenuObject refresh = new MenuObject();
        refresh.setResource(R.drawable.ic_refresh_blue_36dp);
        menuObjects.add(refresh);

        return menuObjects;
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitle = (TextView) findViewById(R.id.title);

        mMenu = (ImageView) findViewById(R.id.menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuDialogFragment.show(mFragmentManager, "DropDownMenuFragment");
            }
        });

        mSearch = (SearchView) findViewById(R.id.search);
        mSearch.addOnLayoutChangeListener(searchExpandHandler);
        mSearch.setOnQueryTextListener(this);
        mSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mTitle.setVisibility(View.VISIBLE);
                mMenu.setVisibility(View.VISIBLE);
                return false;
            }
        });

        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) mSearch.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search_white_36dp);
    }

    private final View.OnLayoutChangeListener searchExpandHandler = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                   int oldBottom) {
            SearchView searchView = (SearchView) v;
            if (searchView.isIconfiedByDefault() && !searchView.isIconified()) {
                // search got expanded from icon to search box, hide tabs to make space
                mTitle.setVisibility(View.GONE);
                mMenu.setVisibility(View.GONE);
            }
        }
    };

    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            java.net.URL url = new java.net.URL(st);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(is);
            return mBitmap;

        } catch (Exception e) {
            return null;
        }
    }

    private void getLostitemsList() {
        String url = "http://52.68.136.81:3000/api/lostitems";

        progress = ProgressDialog.show(LostitemsListActivity.this, "下載資料", "下載遺失物列表中，請稍待片刻...", true);
        ApiRequestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                /** If the response is JSONObject instead of expected JSONArray */
                progress.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray lostitemsList) {
                lostitems = lostitemsList;
                String use_to_set;
                // int pos[];
                pos = new int[lostitems.length()];
                int set_for_list = 0;
                for (int i = 0; i < lostitems.length(); i++) {
                    try {
                        JSONObject lostitem = (JSONObject) lostitems.get(i);

                        HashMap<String, Object> item = new HashMap<String, Object>();

                        item.put("mail", lostitem.getString("mail"));
                        item.put("contact", lostitem.getString("contact"));
                        item.put("description", lostitem.getString("description"));
                        item.put("created_at", lostitem.getString("created_at"));


                        JSONObject category = lostitem.getJSONObject("category");
                        item.put("category", category.getString("name"));


                        JSONObject picture = lostitem.getJSONObject("picture");
                        JSONObject picture2 = picture.getJSONObject("picture");
                        JSONObject thumb = picture2.getJSONObject("thumb");
                        String temp = "http://img.hexun.com.tw/2011-06-01/130166523.jpg";
                        Bitmap img = convertStringToIcon(temp);

                        item.put("thumb", img);
                        //    item.put("picture", picture.getString("picture.url"));
                        // if(i >= 1){
                        switch (setting) {
                            case 0:
                                pos[set_for_list] = i;
                                set_for_list++;
                                list.add(item);

                                break;
                            case 1:

                                if (category.getString("id").equals("1")) {
                                    pos[set_for_list] = i;
                                    set_for_list++;
                                    list.add(item);
                                }
                                break;
                            case 2:
                                if (category.getString("id").equals("2")) {
                                    pos[set_for_list] = i;
                                    set_for_list++;
                                    list.add(item);
                                }
                                break;
                            case 3:
                                if (category.getString("id").equals("3")) {
                                    pos[set_for_list] = i;
                                    set_for_list++;
                                    list.add(item);
                                }
                                break;
                            case 4:
                                if (category.getString("id").equals("4")) {
                                    pos[set_for_list] = i;
                                    set_for_list++;
                                    list.add(item);
                                }
                                break;
                            case 5:
                                if (category.getString("id").equals("5")) {
                                    pos[set_for_list] = i;
                                    set_for_list++;
                                    list.add(item);
                                }
                                break;
                            case 6:
                                if (category.getString("id").equals("6")) {
                                    pos[set_for_list] = i;
                                    set_for_list++;
                                    list.add(item);
                                }
                                break;

                        }
                        //}
                        // else  list.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setSimpleAdapter();
                progress.dismiss();
            }
        });
    }

    private void setSimpleAdapter() {
        adapter = new LostitemListAdapter(getApplicationContext(),
                list,
                R.layout.lostitems_row,
                new String[]{"category", "description", "contact", "thumb"},
                new int[]{R.id.lostitem_category, R.id.textView_description, R.id.textView_contact, R.id.imageView}
        );

        adapter.setViewBinder(new ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {

                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                } else
                    return false;
            }
        });

        mListView.setAdapter(adapter);

        //    thumbImageView.setImageBitmap();
        //    mListView.setAdapter(adapter);
        // thumbImageView.setImageBitmap( convertStringToIcon(temp));

        mListView.setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                JSONObject lostitem = (JSONObject) lostitems.get(pos[position]);
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

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        // ContentView has loaded
        if (!isInitialized) {
            isInitialized = true;
            initUI();
            update(null);
        }
    }

    private void initUI() {

    }

    public void update(View view) {
        if (!ToolsHelper.isNetworkAvailable(this)) {
            ToolsHelper.showNetworkErrorMessage(this);
            finish();
        } else {
//            new UpdateTask(this).execute(getType());
//            findViews();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //mAdapter.notifyDataSetChanged();
    }

    private class ViewHolder {
        // public TextView text;
        public ImageView image;
    }

    private void findView() {
        //final ViewHolder holder;
        String imageUri = "http://52.68.136.81:3000/uploads/lostitem/picture/4/thumb_ArchLinux.png";
        mListView = (ListView) findViewById(R.id.lostitem_list);
        mListView.setEmptyView(findViewById(R.id.no_item_msg));
        //  textViewCategory = (TextView) findViewById(R.id.lostitem_category);
        //   textViewContact = (TextView) findViewById(R.id.textView_contact);
        //    textViewDescription = (TextView) findViewById(R.id.textView_description);
        thumbImageView = (ImageView) findViewById(R.id.imageView);
        //view.setTag(holder);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //  .showStubImage(R.drawable.stub)
                //  .showImageForEmptyUri(R.drawable.empty)
                //  .showImageOnFail(R.drawable.error).cacheInMemory()
                .cacheOnDisc().displayer(new RoundedBitmapDisplayer(5)).build();
        //  ImageLoader.displayImage(
        //      imageUri   ,  thumbImageView, options);
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        String[] lostitem_category = getResources().getStringArray(R.array.lostitem_category);
        Intent refresh = new Intent(this, LostitemsListActivity.class);
        switch (position) {
            case 0:
                break;
            case 1:
                mTitle.setText(lostitem_category[0]);
                setting = 1;
                list.clear();
                getLostitemsList();
                break;
            case 2:
                mTitle.setText(lostitem_category[1]);

                setting = 2;
                list.clear();
                getLostitemsList();
                break;
            case 3:
                mTitle.setText(lostitem_category[2]);

                setting = 3;
                list.clear();
                getLostitemsList();
                break;
            case 4:
                mTitle.setText(lostitem_category[3]);

                setting = 4;
                list.clear();
                getLostitemsList();
                break;
            case 5:
                mTitle.setText(lostitem_category[4]);

                setting = 5;
                list.clear();
                getLostitemsList();
                break;
            case 6:
                mTitle.setText(lostitem_category[5]);

                setting = 6;

                list.clear();
                getLostitemsList();
                break;
            case 7:
                setting = 0;
                //Intent refresh = new Intent(this, LostitemsListActivity.class);
                startActivity(refresh);
                this.finish();
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View view, int i) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
