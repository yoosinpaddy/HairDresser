package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.OrderStoreResponse;
import apps.trichain.hairdresser.storage.repositories.AddressRepository;
import apps.trichain.hairdresser.storage.repositories.CartRepository;
import apps.trichain.hairdresser.user.adapters.CartAdapter;
import apps.trichain.hairdresser.user.models.Address;
import apps.trichain.hairdresser.user.models.Cart;
import apps.trichain.hairdresser.user.models.Image;
import apps.trichain.hairdresser.utils.AlertDialogHelper;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import apps.trichain.hairdresser.utils.PayPalConfig;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.user.activities.OrdersCategoryActivity.PAYPAL_REQUEST_CODE;
import static apps.trichain.hairdresser.utils.AppUtils.FormatCurrency;
import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class OrderSummary extends AppCompatActivity implements View.OnClickListener, AlertDialogHelper.AlertDialogListener {

    private static final String TAG = "OrdeSummary";
    TextView priceSummary,city,phone,locationDescription,add_address,change_address,change_schedule,totalservices,tv_from,tv_to;
    String str_startDate, str_endDate,str_desc,str_city,str_phone;
    RecyclerView finalSummaryItems;
    RelativeLayout schedulelayout;
    MaterialButton placeOrder;
    ImageView back;
    CartRepository cartRepository;
    AddressRepository addressRepository;
    CartAdapter cartAdapter;
    LinearLayout empty_address,existing_address;
    ProgressBar progress;
    AlertDialogHelper alertDialogHelper;
    private  List<String> servicesids;
    private  List<Cart> itemsToDelete;
    private ApiService apiService;
    Call<OrderStoreResponse> orderStoreResponseCall;
    String str_token;
    SharedPrefManager sharedPrefManager;
    private Integer total = 0;
    private boolean isAddress =false;
    private boolean isSchedule =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        initUi();
        FetchCartItems();
        fetchAddressDetails();
    }

    private void initUi(){
        empty_address =findViewById(R.id.empty_address);
        existing_address =findViewById(R.id.existing_address);
        change_schedule =findViewById(R.id.change_schedule);
        priceSummary =findViewById(R.id.priceSummary);
        totalservices =findViewById(R.id.totalservices);
        city =findViewById(R.id.city);
        phone =findViewById(R.id.phone);
        finalSummaryItems =findViewById(R.id.finalSummaryItems);
        placeOrder =findViewById(R.id.placeOrder);
        add_address =findViewById(R.id.add_address);
        change_address =findViewById(R.id.change_address);
        locationDescription =findViewById(R.id.locationDescription);
        tv_from =findViewById(R.id.tv_from);
        tv_to =findViewById(R.id.tv_to);
        schedulelayout =findViewById(R.id.schedulelayout);
        back =findViewById(R.id.back);
        progress =findViewById(R.id.progress);
        back.setOnClickListener(this);
        placeOrder.setOnClickListener(this);
        add_address.setOnClickListener(this);
        change_address.setOnClickListener(this);
        change_schedule.setOnClickListener(this);
        cartRepository =new CartRepository(this);
        addressRepository =new AddressRepository(this);
        alertDialogHelper = new AlertDialogHelper(this);
        apiService = AppUtils.getApiService();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        str_token = "Bearer" + " " + sharedPrefManager.getUserToken();

    }

    @Override
    public void onClick(View v) {
        Intent address = new Intent(OrderSummary.this, AddressActivity.class);
        switch (v.getId()){
            case R.id.placeOrder:
                if (!isAddress){
                    displayToast(OrderSummary.this,false,"Your Address is required");
                }else if (!isSchedule){
                    displayToast(OrderSummary.this,false,"Schedule is required");
                }else {
                    if (NetworkUtils.getConnectivityStatus(this)){
                        postOrder();
                    }else displayToast(OrderSummary.this,false,"No internet connection");
                }
                break;
            case R.id.add_address:
                this.startActivityForResult(address, 1);
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
               break;
            case R.id.change_address:
                this.startActivityForResult(address, 2);
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                break;
            case R.id.change_schedule:
                changeSchedule();
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                fetchAddressDetails();
            }
        }else if(requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                fetchAddressDetails();
            }
        }
    }

    private void FetchCartItems(){
        cartRepository.getItems().observe(this, cartList -> {
            assert cartList != null;
            if(cartList.size() > 0) {
                initRecylerView(cartList);
                setPriceSummary(cartList);
            } else{
//                displayToast(OrderSummary.this,false,"No Items in cart!");
            }
        });
    }

    private void fetchAddressDetails(){
        addressRepository.getFullAddress().observe(this, addressList -> {
            assert addressList != null;
            if(addressList.size() > 0) {
                hideView(empty_address);
                showView(existing_address);
                intAddresses(addressList);
                isAddress=true;
            } else {
                hideView(existing_address);
                showView(empty_address);
                isAddress=false;
            }
        });
    }

    private void initRecylerView(List<Cart> cartList){
        cartAdapter = new CartAdapter(OrderSummary.this,cartList);
        finalSummaryItems.setLayoutManager( new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        finalSummaryItems.setItemAnimator(new DefaultItemAnimator());
        finalSummaryItems.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

    }

    private void setPriceSummary(List<Cart> cartList){
        servicesids =new ArrayList<>();
        itemsToDelete =new ArrayList<>();
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            total += cart.getPrice();
            servicesids.add(cart.getServiceId());
            itemsToDelete.add(cart);
        }
        String totalItems =String.valueOf(cartList.size());
        String pricing =  FormatCurrency(total);
        totalservices.setText(totalItems);
        priceSummary.setText(pricing);
    }

    private void intAddresses(List<Address> addressList){
        for (int i = 0; i < addressList.size(); i++) {
            Address address = addressList.get(i);
            city.setText(address.getCity());
            str_city =address.getCity();
            locationDescription.setText(address.getDescription());
            str_desc =address.getDescription();
            phone.setText(address.getPhone());
            str_phone = address.getPhone();
        }
    }



    private void changeSchedule(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.item_dialog, null);
        builder.setView(layout);

        final TextInputEditText date_from = layout.findViewById(R.id.date_from);
        final TextInputEditText date_to = layout.findViewById(R.id.date_to);
        final TextInputLayout tolayout = layout.findViewById(R.id.tolayout);
        final TextInputLayout fromlayout = layout.findViewById(R.id.fromlayout);
        final MaterialButton save = layout.findViewById(R.id.save);

        //        builder.setPositiveButton("ok ", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        date_from.setOnClickListener(v -> {
            int mYear, mMonth, mDay, mHour, mMinute;
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(OrderSummary.this,
                    (view, year, monthOfYear, dayOfMonth) -> date_from.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();

        });

        date_to.setOnClickListener(v -> {
            int mYear, mMonth, mDay;
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(OrderSummary.this,
                    (view, year, monthOfYear, dayOfMonth) -> date_to.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });
        save.setOnClickListener(v -> {
            String start_date = Objects.requireNonNull(date_from.getText()).toString();
            String end_date = Objects.requireNonNull(date_to.getText()).toString();
           if (start_date.isEmpty()){
               fromlayout.setError("Required");
               date_from.requestFocus();
           }else if(end_date.isEmpty()){
               tolayout.setError("required");
               date_to.requestFocus();
           }else {
               setSchedule(start_date,end_date);
               dialog.dismiss();
           }
        });
    }

    private void  setSchedule(String fromdate,String todate){
        tv_from.setText(fromdate);
        tv_to.setText(todate);
        str_startDate = fromdate;
        str_endDate =todate;
        isSchedule = true;
    }

    private void postOrder(){
        Log.e(TAG, "postorder: ");
        disableUI(true);
        orderStoreResponseCall = apiService.postOrder(servicesids,str_endDate,str_endDate,str_desc,str_city,str_phone,str_token);
        orderStoreResponseCall.enqueue(new Callback<OrderStoreResponse>() {
            @Override
            public void onResponse(Call<OrderStoreResponse> call, Response<OrderStoreResponse> response) {
                disableUI(false);
                OrderStoreResponse msg = response.body();
                if (response.isSuccessful()) {
                    Log.e("orderposted", "......" + response);
                    if (msg != null) {
                        if (!msg.getError()) {
                            AppUtils.displayToast(OrderSummary.this, true, msg.getMessage());
                            emptyCart(itemsToDelete);

                        } else {
                            AppUtils.displayToast(OrderSummary.this, false, msg.getMessage());
                        }
                    }
                } else {
                    AppUtils.displayToast(OrderSummary.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(OrderSummary.this, false, "Please try Again");

                }
            }

            @Override
            public void onFailure(Call<OrderStoreResponse> call, Throwable t) {
                disableUI(false);

                Log.e(TAG, "onFailure: " + call.request().url().toString());
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: Canceled! " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailure: Failed! " + t.getLocalizedMessage());
                }

            }
        });
    }

    private void disableUI(Boolean value) {
        if (value) {
            placeOrder.setEnabled(false);
            hideView(placeOrder);
            showView(progress);
        } else {
            placeOrder.setEnabled(true);
            hideView(progress);
            showView(placeOrder);
        }


    }

    private  void emptyCart(List<Cart> cartList1){
        for (int i = 0; i < cartList1.size(); i++) {
            Cart cart = cartList1.get(i);
            Log.e("emptyCart: serviceid",cartList1.get(i).getServiceId());
//            cartRepository.deleteItemByServiceId(Cartservicesids.get(i));
            cartRepository.deleteCartItem(cart);
        }
        startActivity(new Intent(OrderSummary.this, MyOrdersActivity.class));
        overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
        finish();

    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onPositiveClick(int from) {

    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
}
