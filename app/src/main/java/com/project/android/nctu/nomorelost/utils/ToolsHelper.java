package com.project.android.nctu.nomorelost.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by chichunchen on 5/25/15.
 */
public class ToolsHelper {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showNetworkErrorMessage(Context context) {
        Toast.makeText(context, "偵測不到網路，請確認是否連上網路。", Toast.LENGTH_LONG).show();
    }

    public static void showSiteErrorMessage(Context context) {
        Toast.makeText(context, "連不上伺服器，請稍候再重新整理一次。", Toast.LENGTH_LONG).show();
    }
}
