package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.OrderResponse;
import apps.trichain.hairdresser.user.models.Order;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class MyOrdersActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "MyOrders" ;
    TextView completedOrderCount, waitingApprovalOrderCount,inProgressOrderCount,PaymentOrderCount;
    RelativeLayout completedOrder, waitingApprovalOrder,inProgressOrder,PaymentOrder;
    private ProgressBar progress;
    private TextView retrybtn;
    private ImageView back;
    private ScrollView  mainscroll;
    private List<Order> orderList;
    private List<Order> InProgressOrderList;
    private List<Order> waitingApprovalOrderList;
    private List<Order> PaymentOrderList;
    private List<Order> completedOrderList;
    private ApiService apiService;
    Call<OrderResponse> orderResponseCall;
    String str_token;
    SharedPrefManager sharedPrefManager;
    Integer completedOrders =0;
    Integer waitingapprovalOrders = 0;
    Integer inProgressOrders = 0;
    Integer PaymentOrders = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        initUi();
        fetchOrders();
    }

    private void initUi(){
        completedOrderCount =findViewById(R.id.completedOrderCount);
        waitingApprovalOrderCount =findViewById(R.id.waitingApprovalOrderCount);
        inProgressOrderCount =findViewById(R.id.inProgressOrderCount);
        PaymentOrderCount =findViewById(R.id.PaymentOrderCount);
        progress =findViewById(R.id.progress);
        mainscroll =findViewById(R.id.mainscroll);
        inProgressOrder =findViewById(R.id.inProgressOrder);
        waitingApprovalOrder =findViewById(R.id.waitingApprovalOrder);
        PaymentOrder =findViewById(R.id.PaymentOrder);
        completedOrder =findViewById(R.id.completedOrder);
        back =findViewById(R.id.back);
        retrybtn =findViewById(R.id.retrybtn);
        inProgressOrder.setOnClickListener(this);
        completedOrder.setOnClickListener(this);
        waitingApprovalOrder.setOnClickListener(this);
        PaymentOrder.setOnClickListener(this);
        back.setOnClickListener(this);
        retrybtn.setOnClickListener(this);

        apiService = AppUtils.getApiService();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        str_token = "Bearer" + " " + sharedPrefManager.getUserToken();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.inProgressOrder:
                if ( inProgressOrders > 0){
                    goToOrder(InProgressOrderList,2);
                }else displayToast(MyOrdersActivity.this,false,"You have no orders in progress");
                break;
            case R.id.completedOrder:
                if (completedOrders >0 ){
                    goToOrder(completedOrderList,3);
                }else displayToast(MyOrdersActivity.this,false,"You have no completed orders");
                break;
            case R.id.waitingApprovalOrder:
                if (waitingapprovalOrders > 0){
                    goToOrder(waitingApprovalOrderList,1);
                }else displayToast(MyOrdersActivity.this,false,"You have no orders waiting approval");
                break;
            case R.id.PaymentOrder:
                if (PaymentOrders > 0){
                    goToOrder(PaymentOrderList,4);
                }else displayToast(MyOrdersActivity.this,false,"You have no orders waiting for payment");
                break;
            case R.id.retrybtn:
                if (NetworkUtils.getConnectivityStatus(MyOrdersActivity.this)) {
                    clearOnRefresh();
                    fetchOrders();
                }else displayToast(MyOrdersActivity.this,false,"No internet connection!");
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    private void fetchOrders(){
        Log.e(TAG, "fetchorders: ");
        hideView(mainscroll);
        hideView(retrybtn);
        showView(progress);
        orderResponseCall = apiService.getOrders(str_token);
        orderResponseCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                hideView(progress);
                showView(mainscroll);
                showView(retrybtn);
                OrderResponse msg = response.body();
                if (response.isSuccessful()) {
                    Log.e("ordersfetched", "......" + response);
                    if (msg != null) {
                        if (!msg.getError()) {
//                            AppUtils.displayToast(MyOrdersActivity.this, true, msg.getMessage());

                            populateOrderCount(msg.getOrderList());
                        } else {
                            AppUtils.displayToast(MyOrdersActivity.this, false, msg.getMessage());
                        }
                    }
                } else {
                    AppUtils.displayToast(MyOrdersActivity.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(MyOrdersActivity.this, false, "Please try Again");

                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                hideView(progress);
                showView(retrybtn);

                Log.e(TAG, "onFailure: " + call.request().url().toString());
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: Canceled! " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailure: Failed! " + t.getLocalizedMessage());
                }

            }
        });
    }

    private void populateOrderCount(List<Order> orderList1){
        waitingApprovalOrderList = new ArrayList<>();
        InProgressOrderList =new ArrayList<>();
        PaymentOrderList= new ArrayList<>();
        completedOrderList =new ArrayList<>();
        orderList =orderList1;
        for (int i = 0; i < orderList1.size(); i++) {
            Order order = orderList1.get(i);
            Log.e("populateorder: order","---"+order.toString() );
            if (order.getStatus() == 2){
                InProgressOrderList.add(order);
                inProgressOrders++;
            }else if(order.getStatus() == 3 || order.getStatus() == 5){
                completedOrderList.add(order);
                completedOrders++;
            }else if (order.getStatus() == 1){
                waitingApprovalOrderList.add(order);
                waitingapprovalOrders++;
            }else if (order.getStatus() == 4){
                PaymentOrderList.add(order);
                PaymentOrders ++;
            }
        }
        completedOrderCount.setText(String.valueOf(completedOrders));
        waitingApprovalOrderCount.setText(String.valueOf(waitingapprovalOrders));
        inProgressOrderCount.setText(String.valueOf(inProgressOrders));
        PaymentOrderCount.setText(String.valueOf(PaymentOrders));
    }
    private void clearOnRefresh(){
        inProgressOrders =0;
        completedOrders =0;
        waitingapprovalOrders =0;
        PaymentOrders =0;
        orderList.clear();
        waitingApprovalOrderList.clear();
        InProgressOrderList.clear();
        PaymentOrderList.clear();
        completedOrderList.clear();

    }

    private void goToOrder(List<Order> orderList1, Integer status){
        Intent intent = new Intent(MyOrdersActivity.this,OrdersCategoryActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("orders", (Serializable) orderList1);
        extras.putInt("status", status);
        intent.putExtras(extras);
        startActivity(intent);
        overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
    }
}
