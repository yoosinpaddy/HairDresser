package apps.trichain.hairdresser.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

import apps.trichain.hairdresser.user.models.User;

public class SharedPrefManager {
    private static final String SHARED_PREFS_NAME = "hairdresser";
    private static SharedPrefManager mInstance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void customerLogin(Boolean isLoggedIn, User user) {
        sharedPreferences.edit()
                .putBoolean("isLoggedIn", isLoggedIn)
                .putInt("user_id", user.getId())
                .putString("name", user.getName())
                .putString("email", user.getEmail())
                .putString("phone", user.getPhone())
                .putString("city", user.getCity())
                .putString("user_type", user.getUserType())
                .putString("created_at", user.getCreatedAt())
                .putString("token", user.getToken())
                .apply();
    }

    public void customerLocation(String location){
        sharedPreferences.edit()
                .putString("gpslocation",location)
                .apply();
    }


    public String[] getCustomerInformation() {
        return new String[]{
                sharedPreferences.getString("name", null)
        };
    }

    public String getGpsLocation(){
        return sharedPreferences.getString("gpslocation",null);
    }
    public String getUserToken(){
        return sharedPreferences.getString("token", null);
    }


    public Boolean isLogin() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public Boolean logoutCustomer() {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
        return true;
    }

    public void saveCustomerData(String serializedData) {
        sharedPreferences.edit().putString("customer_data", serializedData).apply();
    }

    public String getCustomerData() {
        return sharedPreferences.getString("customer_data", null);
    }

    public void customerLogout() {
        try {
            sharedPreferences.getAll().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}