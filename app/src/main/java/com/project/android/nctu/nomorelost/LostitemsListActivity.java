package com.project.android.nctu.nomorelost;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IntegerRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.loopj.android.http.AsyncHttpResponseHandler;
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
import android.widget.Toast;

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
    private ArrayList<HashMap<String, Object>> mSearchList =  new ArrayList<HashMap<String, Object>>();
    private boolean isInitialized = false;
    private FragmentManager mFragmentManager;
    private DialogFragment mMenuDialogFragment;
    private TextView mTitle;
    private SearchView mSearch;
    private ImageView mMenu, thumbImageView;
    private LostitemListAdapter adapter;
    AlertDialog.Builder alert;
    private TextView textViewCategory, textViewdate, textViewtitle;
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
        mSearch.setSubmitButtonEnabled(false);
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

                        item.put("id", lostitem.getString("id"));
                        item.put("title", lostitem.getString("title"));
                        item.put("mail", lostitem.getString("mail"));
                        item.put("contact", lostitem.getString("contact"));
                        item.put("title", lostitem.getString("title"));
                        item.put("created_at", lostitem.getString("created_at"));
                        item.put("num",i);
                        JSONObject category = lostitem.getJSONObject("category");
                        item.put("category", category.getString("name"));
                        item.put("cate_ID", category.getString("id"));
                        JSONObject picture = lostitem.getJSONObject("picture");
                        JSONObject picture2 = picture.getJSONObject("picture");
                        JSONObject thumb = picture2.getJSONObject("thumb");
                        String imageUri = "http://52.68.136.81:3000/" + thumb.getString("url");
                        String temp = "http://img.hexun.com.tw/2011-06-01/130166523.jpg";
                        Bitmap img = convertStringToIcon(temp);

                        item.put("thumb", imageUri);
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
                new String[]{"category", "title", "date", "thumb"},
                new int[]{R.id.lostitem_category, R.id.textView_title, R.id.textView_date, R.id.imageView}
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
        mListView.setOnItemLongClickListener(itemLongClickListener);
    }
    private void setSimpleAdapter2() {
        adapter = new LostitemListAdapter(getApplicationContext(),
                mSearchList,
                R.layout.lostitems_row,
                new String[]{"category", "title", "date", "thumb"},
                new int[]{R.id.lostitem_category, R.id.textView_title, R.id.textView_date, R.id.imageView}
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
        mListView.setOnItemLongClickListener(itemLongClickListener);
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

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            // find item id
            HashMap<String, Object> item = new HashMap<String, Object>();
            item = list.get(position);
            String item_id = item.get("id").toString();
            final String del_url = "http://52.68.136.81:3000/api/lostitems/" + item_id;

            // define alert
            alert = new AlertDialog.Builder(LostitemsListActivity.this);
            alert.setTitle("請輸入驗證碼");
            alert.setMessage("驗證碼：");
            // Set an EditText view to get user input
            final EditText input = new EditText(LostitemsListActivity.this);
            alert.setView(input);
            alert.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // delete the item
                    String confirm = input.getText().toString();
                    String url = del_url + "?lostitem[confirm]=" + confirm;

                    ApiRequestClient.delete(getApplicationContext(), url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            // Successfully got a response
                            Toast.makeText(getApplicationContext(), "已成功刪除！", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                                error) {
                            // Response failed :(
                            Toast.makeText(getApplicationContext(), "請再次檢驗您的驗證碼是否正確。", Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent refresh = new Intent(LostitemsListActivity.this, LostitemsListActivity.class);
                    startActivity(refresh);
                }
            });
            alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.show();

            Toast.makeText(getApplicationContext(), item_id, Toast.LENGTH_SHORT).show();
            return false;
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
      //  mListView.setTextFilterEnabled(true);
        mListView.setEmptyView(findViewById(R.id.no_item_msg));
        textViewCategory = (TextView) findViewById(R.id.lostitem_category);
        textViewdate = (TextView) findViewById(R.id.textView_date);
        textViewtitle = (TextView) findViewById(R.id.textView_title);
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
    public boolean onQueryTextChange(String newText) {
        mSearchList.clear();
        Object[] obj = searchItem(newText);
        updateLayout(obj);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }


    public Object[] searchItem(String name) {
        //pos = new int[lostitems.length()];
        mSearchList.clear();
        int set_for_list=0;
            for (int i = 0; i < list.size(); i++) {
           int index = list.get(i).get("title").toString().indexOf(name);
            // 存在匹配的数据
            if (index != -1) {
                /*switch (setting) {
                case 0:
                   String temp =  list.get(i).get("num").toString();
                    int num = Integer.parseInt(temp);
                    pos[set_for_list] = num;
                    set_for_list++;
                    mSearchList.add(list.get(i));

                    break;
                case 1:

                    if (list.get(i).get("cate_ID").toString().equals("1")) {
                        pos[set_for_list] = i;
                        set_for_list++;
                        mSearchList.add(list.get(i));
                    }
                    break;
                case 2:
                    if (list.get(i).get("cate_ID").toString().equals("2")) {
                        pos[set_for_list] = i;
                        set_for_list++;
                        mSearchList.add(list.get(i));
                    }
                    break;
                case 3:
                    if (list.get(i).get("cate_ID").toString().equals("3")) {
                        pos[set_for_list] = i;
                        set_for_list++;
                    mSearchList.add(list.get(i));
                    }
                    break;
                case 4:
                    if (list.get(i).get("cate_ID").toString().equals("4")) {
                        pos[set_for_list] = i;
                        set_for_list++;
                        mSearchList.add(list.get(i));
                    }
                    break;
                case 5:
                    if (list.get(i).get("cate_ID").toString().equals("5")) {
                        pos[set_for_list] = i;
                        set_for_list++;
                        mSearchList.add(list.get(i));
                    }
                    break;
                case 6:
                    if (list.get(i).get("cate_ID").toString().equals("6")) {
                        pos[set_for_list] = i;
                        set_for_list++;
                        mSearchList.add(list.get(i));
                    }
                    break;

            }*/
             //   pos[set_for_list] = i;

             //   set_for_list++;
                String temp =  list.get(i).get("num").toString();
                int num = Integer.parseInt(temp);
                pos[set_for_list] = num;
                set_for_list++;
                mSearchList.add(list.get(i));
            }
        }
        setSimpleAdapter2();
        return mSearchList.toArray();
    }

    public void updateLayout(Object[] obj) {
        mListView.setAdapter(adapter);
    }}

