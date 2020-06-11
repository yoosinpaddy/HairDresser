package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.OrderResponse;
import apps.trichain.hairdresser.network.responses.OrderResponse;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class ConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "confirmActivity";
    MaterialButton finish;
    ProgressBar progress;
    LinearLayout mainview,error_layout;
    TextView txt_message,retry;
    private ApiService apiService;
    Call<OrderResponse> orderResponseCall;
    String str_token,paypal_id,order_d;
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        finish =findViewById(R.id.finish);
        mainview =findViewById(R.id.mainview);
        error_layout =findViewById(R.id.error_layout);
        txt_message =findViewById(R.id.txt_message);
        retry =findViewById(R.id.retry);
        progress =findViewById(R.id.progress);
        apiService = AppUtils.getApiService();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        str_token = "Bearer" + " " + sharedPrefManager.getUserToken();
        //Getting Intent
        Intent intent = getIntent();
            order_d =intent.getStringExtra("orderid");
        try {
            JSONObject jsonDetails = new JSONObject(Objects.requireNonNull(intent.getStringExtra("PaymentDetails")));

            //Displaying payment details
            showDetails(jsonDetails.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finish.setOnClickListener(this);
        retry.setOnClickListener(this);
        updatePaypalId();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.finish:
                startActivity(new Intent(ConfirmationActivity.this,ServicesActivity.class));
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                finish();
                break;
            case R.id.retry:
                if (NetworkUtils.getConnectivityStatus(this)){
                    updatePaypalId();
                }else displayToast(ConfirmationActivity.this,false,"No internet connection");
                break;
        }
    }

    private void updatePaypalId(){
        Log.e(TAG, "papypalId: ");
        hideView(mainview);
        hideView(error_layout);
        showView(progress);
        orderResponseCall = apiService.updateOrder(paypal_id,order_d,str_token);
        orderResponseCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                hideView(progress);
                OrderResponse msg = response.body();
                if (response.isSuccessful()) {
                    showView(mainview);
                    Log.e("orderposted", "......" + response);
                    if (msg != null) {
                        if (!msg.getError()) {
                            AppUtils.displayToast(ConfirmationActivity.this, true, msg.getMessage());
                            sharedPrefManager.storePaypalId(paypal_id);
                            sharedPrefManager.initUpdateStatus(true);
                        } else {
                            AppUtils.displayToast(ConfirmationActivity.this, false, msg.getMessage());
                        }
                    }
                } else {
                    sharedPrefManager.initUpdateStatus(false);
                    showView(error_layout);
                    Log.e("errortrue", "......" + response);
                    AppUtils.displayToast(ConfirmationActivity.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(ConfirmationActivity.this, false, "Please try Again");

                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                hideView(progress);
                showView(error_layout);
                sharedPrefManager.initUpdateStatus(false);
                Log.e(TAG, "onFailure: " + call.request().url().toString());
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: Canceled! " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailure: Failed! " + t.getLocalizedMessage());
                }

            }
        });
    }

    private void showDetails(JSONObject jsonDetails, String paymentAmount) throws JSONException {
//        //Views
//        TextView textViewId = (TextView) findViewById(R.id.paymentId);
//        TextView textViewStatus= (TextView) findViewById(R.id.paymentStatus);
//        TextView textViewAmount = (TextView) findViewById(R.id.paymentAmount);
//
        //Showing the details from json object
        paypal_id =jsonDetails.getString("id");
//        textViewId.setText(jsonDetails.getString("id"));
//        textViewStatus.setText(jsonDetails.getString("state"));
//        textViewAmount.setText(paymentAmount+" USD");
    }
}