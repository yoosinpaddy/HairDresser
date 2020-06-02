package apps.trichain.hairdresser.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {


    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;

    public static Boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        int net_type = 0;
        if (null != activeNetworkInfo) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

//    public static String getConnectivityStatusString(Context context) {
//        int conn = getConnectivityStatus(context);
//        String status = null;
//        if (conn == TYPE_WIFI)
//            status = "Connected to wifi";
//        else if (conn == TYPE_MOBILE)
//            status = "Connected to mobile data";
//        else if (conn == TYPE_NOT_CONNECTED)
//            status = "Not connected";
//
//        return status;
//    }
}
