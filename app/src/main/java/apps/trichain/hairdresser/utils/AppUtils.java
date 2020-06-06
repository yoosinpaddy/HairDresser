package apps.trichain.hairdresser.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.RetrofitClient;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class AppUtils {

    private static final String TAG = "AppUtils";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
//    public static final String BASE_URL = "http://192.168.43.106:8000/api/";
    public static final String BASE_URL = "http://admin.jamesrobin.gadaiweb.com/api/";
//    public static final String BASE_URL = "http://api.mati.co.ke/hairstylist/public/api/";
    static boolean alreadyAnimatedDown, alreadyAnimatedUp;

    public static ApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }

    public static void displayToast(Activity activity, boolean isSuccess, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout;
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        if (isSuccess) {
            layout = inflater.inflate(R.layout.item_success_toast, null);
        } else {
            layout = inflater.inflate(R.layout.item_failure_toast, null);
        }
        toast.setView(layout);
        TextView tv_message = layout.findViewById(R.id.tv_message);
        tv_message.setText(message);
        toast.show();
    }

    public static String formatDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat fromInput = new SimpleDateFormat("yyyy-MM-dd");

        String reformattedStr = "";
        String mDate = "";
        int spacePos = date.indexOf(" ");
        if (spacePos > 0) {
            mDate = date.substring(0, spacePos);
        }

        try {
            Date fromUser = fromInput.parse(mDate);
            reformattedStr = dateFormat.format(fromUser);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return reformattedStr;
    }

    public static String getRelativeTime(String date) {
        if (date != null) {
            Date dte;
            long milliseconds = 0;
            try {
                dte = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
                milliseconds = dte.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long now = System.currentTimeMillis();

            final long diff = now - milliseconds;

            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 59 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < (60 + 1) * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 120 * HOUR_MILLIS) {
                return diff / DAY_MILLIS + "Days ago";
            } else if (diff < 240 * HOUR_MILLIS) {
                return "a week ago";
            } else {
                return "weeks ago";
            }
        } else {
            return "";
        }
    }


    public static void showView(View v) {
        if (v.getVisibility() == GONE || v.getVisibility() == INVISIBLE) {
            v.setVisibility(VISIBLE);
            v.animate()
                    .alpha(1.0f)
                    .setDuration(500);
        }
    }

    public static void hideView(View v) {
        if (v.getVisibility() == VISIBLE) {
            v.animate()
                    .alpha(0.0f)
                    .setDuration(500);
            v.setVisibility(GONE);
        }
    }

    public static String FormatCurrency(int raw) {
        String raw_str = String.valueOf(raw);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setCurrency(Currency.getInstance("USD"));
        numberFormat.setMinimumFractionDigits(0);
        String currency = null;
        try {
            currency = String.valueOf(numberFormat.format(numberFormat.parse(raw_str)));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "$ " + currency;
    }


    public static String makePlural(String s) {
        int l = s.length();
        String lastChar;
        try {
            lastChar = String.valueOf(s.charAt(l - 1));
            if (!lastChar.equals("s")) {
                if (lastChar.equals("y"))
                    return s.replace("y", "ies");
                return s + "s";
            } else {
                return s;
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "getLastCharacter: index out of bounds! Exiting...");
            return s;
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }



    public static void showSnackBar(View snackBarView, String message, int bgColor,boolean hasActionButton,
                                    String actionText) {
        Snackbar snackbar = Snackbar.make(snackBarView, message, Snackbar.LENGTH_LONG);
        if (bgColor != 0)
            snackbar.getView().setBackgroundColor(bgColor);
        if (hasActionButton) {
            snackbar.setAction(actionText, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Do something here
                }
            }).show();

        } else {
            snackbar.show();
        }
    }

    public static void animateSlideDownBottomNav(Activity activity, View v) {
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.slide_out_down);
        if (!alreadyAnimatedDown) {
            v.startAnimation(anim);
            alreadyAnimatedDown = true;
            alreadyAnimatedUp = false;
            hideView(v);
        }
        Log.d(TAG, "animateSlideDownBottomNav: performed once");
    }

    public static void animateSlideUpBottomNav(Activity activity, View v) {
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.slide_in_up);
        if (!alreadyAnimatedUp) {
            showView(v);
            v.startAnimation(anim);
            alreadyAnimatedUp = true;
            alreadyAnimatedDown = false;
        }
        Log.d(TAG, "animateSlideUpBottomNav: performed once");
    }

    private static int[] getScreenHeightAndWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.heightPixels, metrics.widthPixels};
    }

    private static File createDirectory(Activity activity, String filename) {
        if (ActivityCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity, WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new
                    String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 1);
            return null;
        } else {
            return activity.getExternalFilesDir(filename);
        }
    }

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static String stringFromNumbers(int... numbers) {
        StringBuilder sNumbers = new StringBuilder();
        for (int number : numbers)
            sNumbers.append(number);
        return sNumbers.toString();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.length() == 0;
    }


    //image operations
    public static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static Uri getImageUri(byte[] image) throws UnsupportedEncodingException {
        byte[] buf = image;
        String mystring = new String(buf, "UTF-8");
        return Uri.parse(mystring);
    }


}
