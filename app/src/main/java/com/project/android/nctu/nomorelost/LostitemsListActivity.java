package com.project.android.nctu.nomorelost;


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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.android.nctu.nomorelost.utils.ApiRequestClient;
import com.project.android.nctu.nomorelost.utils.ToolsHelper;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ImageView mMenu;
    private SimpleAdapter adapter;
    private TextView textViewCategory, textViewContact, textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        menuObjects.add(new MenuObject("生活及運動用品"));
        menuObjects.add(new MenuObject("衣物、手錶與配件"));
        menuObjects.add(new MenuObject("現金、證件、票卡"));
        menuObjects.add(new MenuObject("圖書文具"));
        menuObjects.add(new MenuObject("3C"));
        menuObjects.add(new MenuObject("鑰匙、其他"));

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

        mTitle =  (TextView)findViewById(R.id.title);

        mMenu = (ImageView)findViewById(R.id.menu);
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuDialogFragment.show(mFragmentManager, "DropDownMenuFragment");
            }
        });

        mSearch = (SearchView)findViewById(R.id.search);
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
            SearchView searchView = (SearchView)v;
            if (searchView.isIconfiedByDefault() && !searchView.isIconified())            {
                // search got expanded from icon to search box, hide tabs to make space
                mTitle.setVisibility(View.GONE);
                mMenu.setVisibility(View.GONE);
            }
        }
    };

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

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        // ContentView has loaded
        if(!isInitialized){
            isInitialized = true;
            initUI();
            update(null);
        }
    }

    private void initUI() {

    }

    public void update(View view) {
        if(!ToolsHelper.isNetworkAvailable(this)) {
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
    public void onMenuItemClick(View view, int position) {
        switch (position) {
            case 0:
                break;
            case 1:
//                this.info(null);
                break;
            case 2:
//                this.update(null);
                break;
            case 3:
                break;
            case 4:
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
