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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import apps.trichain.hairdresser.network.responses.OrderResponse;
import apps.trichain.hairdresser.storage.repositories.AddressRepository;
import apps.trichain.hairdresser.storage.repositories.CartRepository;
import apps.trichain.hairdresser.user.adapters.CartAdapter;
import apps.trichain.hairdresser.user.models.Address;
import apps.trichain.hairdresser.user.models.Cart;
import apps.trichain.hairdresser.utils.AlertDialogHelper;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.PayPalConfig;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.FormatCurrency;
import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class OrderSummary extends AppCompatActivity implements View.OnClickListener, AlertDialogHelper.AlertDialogListener {

    private static final String TAG = "OrdeSummary";
    TextView priceSummary,city,phone,locationDescription,add_address,change_address,change_schedule,totalservices,tv_from,tv_to;
    String str_startDate, str_endDate,str_desc,str_city,str_phone;
    RecyclerView finalSummaryItems;
    MaterialButton payment;
    CartRepository cartRepository;
    AddressRepository addressRepository;
    CartAdapter cartAdapter;
    LinearLayout empty_address,existing_address;
    ProgressBar progress;
    AlertDialogHelper alertDialogHelper;
    private  List<String> servicesids;
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
    private Integer total = 0;
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
        payment =findViewById(R.id.payment);
        add_address =findViewById(R.id.add_address);
        change_address =findViewById(R.id.change_address);
        locationDescription =findViewById(R.id.locationDescription);
        tv_from =findViewById(R.id.tv_from);
        tv_to =findViewById(R.id.tv_to);
        progress =findViewById(R.id.progress);
        payment.setOnClickListener(this);
        add_address.setOnClickListener(this);
        change_address.setOnClickListener(this);
        change_schedule.setOnClickListener(this);
        cartRepository =new CartRepository(this);
        addressRepository =new AddressRepository(this);
        alertDialogHelper = new AlertDialogHelper(this);
        apiService = AppUtils.getApiService();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        str_token = "Bearer" + " " + sharedPrefManager.getUserToken();
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        Intent address = new Intent(OrderSummary.this, AddressActivity.class);
        switch (v.getId()){
            case R.id.payment:
//                initiatePayment();
                postOrder();
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
                                .putExtra("PaymentAmount", total));
                        displayToast(OrderSummary.this,true,paymentDetails.toString());
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

    private void FetchCartItems(){
        cartRepository.getItems().observe(this, cartList -> {
            assert cartList != null;
            if(cartList.size() > 0) {
                initRecylerView(cartList);
                setPriceSummary(cartList);
            } else{
                displayToast(OrderSummary.this,false,"No Items in cart!");
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
            } else {
                hideView(existing_address);
                showView(empty_address);
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
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            total += cart.getPrice();
            servicesids.add(cart.getServiceId());
        }
        String totalItems =String.valueOf(cartList.size());
        String pricing =  FormatCurrency(total);
        String pricing1 = FormatCurrency(total);
        totalservices.setText(totalItems);
        priceSummary.setText(pricing);
        payment.setText(pricing1);
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

    private void initiatePayment(){
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

    }

    private void processOrder(){

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
    }

    private void postOrder(){
        Log.e(TAG, "postorder: ");
        disableUI(true);
        orderResponseCall = apiService.postOrder(servicesids, str_startDate,str_endDate,str_desc,str_desc,str_city,str_phone,str_token);
        orderResponseCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                disableUI(false);
                OrderResponse msg = response.body();
                if (response.isSuccessful()) {
                    Log.e("orderposted", "......" + response);
                    if (msg != null) {
                        if (!msg.getError()) {
                            AppUtils.displayToast(OrderSummary.this, true, msg.getMessage());
                            emptyCart(servicesids);
                            startActivity(new Intent(OrderSummary.this, MyOrdersActivity.class));
                            overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                            finish();

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
            public void onFailure(Call<OrderResponse> call, Throwable t) {
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
            payment.setEnabled(false);
            hideView(payment);
            showView(progress);
        } else {
            payment.setEnabled(true);
            hideView(progress);
            showView(payment);
        }


    }

    private  void emptyCart(List<String> Cartservicesids){
        for (int i = 0; i < Cartservicesids.size(); i++) {
            cartRepository.deleteItemByServiceId(Cartservicesids.get(i));
        }

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
