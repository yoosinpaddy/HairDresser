package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.OrderResponse;
import apps.trichain.hairdresser.user.adapters.OrdersAdapter;
import apps.trichain.hairdresser.user.models.Order;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import apps.trichain.hairdresser.utils.PayPalConfig;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class OrdersCategoryActivity extends AppCompatActivity implements View.OnClickListener, OrdersAdapter.RecyclerViewClickListener {

    private static final String TAG = "OrdersCategoryActivity";
    private RecyclerView ordersRecycler;
    private ProgressBar progress;
    private TextView orders_title;
    private ScrollView mainview;
    private ImageView orders_back;
    List<Order> orderList;
    Integer status = 0;
    String order_id = null;
    private OrdersAdapter ordersAdapter;
    private ApiService apiService;
    Call<OrderResponse> orderResponseCall;
    String str_token;
    SharedPrefManager sharedPrefManager;
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
    Integer totalamount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_category);
        initUi();

        Bundle bundle = getIntent().getExtras();
        hideView(mainview);
        showView(progress);
        new Thread() {
            @Override
            public void run() {

                //Do long operation stuff here search stuff
                if (bundle != null) {
                    orderList = (List<Order>) bundle.getSerializable("orders");
                    status = bundle.getInt("status");
                    if (status == 1) {
                        orders_title.setText("Orders Waiting Approval");
                    } else if (status == 2) {
                        orders_title.setText("Orders in Prgoress");
                    } else if (status == 3) {
                        orders_title.setText("Completed Orders");
                    } else if (status == 4) {
                        orders_title.setText("Orders Waiting Paymment");
                    } else {
                        orders_title.setText("Orders");
                    }
                    initRecylerView(orderList);
                }

                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideView(progress);
                            showView(mainview);
                        }
                    });
                } catch (final Exception ex) {

                }
            }
        }.start();


    }

    private void initUi() {
        ordersRecycler = findViewById(R.id.ordersRecycler);
        mainview = findViewById(R.id.mainview);
        progress = findViewById(R.id.progress);
        orders_title = findViewById(R.id.orders_title);
        orders_back = findViewById(R.id.orders_back);
        orders_back.setOnClickListener(this);
        apiService = AppUtils.getApiService();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        str_token = "Bearer" + " " + sharedPrefManager.getUserToken();
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orders_back:
                onBackPressed();
                break;
        }
    }

    private void initRecylerView(List<Order> orderList1) {
        ordersAdapter = new OrdersAdapter(OrdersCategoryActivity.this, orderList1, this);
        ordersRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        ordersRecycler.setItemAnimator(new DefaultItemAnimator());
        ordersRecycler.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();

    }


    private void initiatePayment(Integer total) {
        if (NetworkUtils.getConnectivityStatus(this)) {
            totalamount = total;
            //Creating a paypalpayment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(total)), "USD", "Hair Dressing Fee",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            //Creating Paypal Payment activity intent
            Intent intent = new Intent(this, PaymentActivity.class);
            //putting the paypal configuration to the intent
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            //Puting paypal payment to the intent
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            //Starting the intent activity for result
            //the request code will be used on the method onActivityResult
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }else {displayToast(OrdersCategoryActivity.this,false,"No internet connection!");}

    }

    private void rateOrder(Float rating,String order_d){

        orderResponseCall = apiService.rateOrder(rating,order_d,str_token);
        orderResponseCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                hideView(progress);
                OrderResponse msg = response.body();
                if (response.isSuccessful()) {
                    showView(mainview);
                    Log.e("rated", "......" + response);
                    if (msg != null) {
                        if (!msg.getError()) {
                            AppUtils.displayToast(OrdersCategoryActivity.this, true, msg.getMessage());
                        } else {
                            AppUtils.displayToast(OrdersCategoryActivity.this, false, msg.getMessage());
                        }
                    }
                } else {
                    Log.e("errortrue", "......" + response);
                    AppUtils.displayToast(OrdersCategoryActivity.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(OrdersCategoryActivity.this, false, "Please try Again");

                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
//                hideView(progress);
//                showView(error_layout);
                Log.e(TAG, "onFailure: " + call.request().url().toString());
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: Canceled! " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailure: Failed! " + t.getLocalizedMessage());
                }

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", totalamount)
                                .putExtra("orderid", order_id));
                        finish();
//                        displayToast(OrdersCategoryActivity.this,true,paymentDetails.toString());
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


    @Override
    public void makePayment(View v, int position, Integer amount, String orderId) {
        initiatePayment(amount);
        order_id = orderId;
    }

    @Override
    public void onRatingBarChange(View v, int position, String orderId, float value) {
        rateOrder(value,orderId);
    }

}
