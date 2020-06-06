package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.storage.repositories.AddressRepository;
import apps.trichain.hairdresser.storage.repositories.CartRepository;
import apps.trichain.hairdresser.user.adapters.CartAdapter;
import apps.trichain.hairdresser.user.models.Address;
import apps.trichain.hairdresser.user.models.Cart;

import static apps.trichain.hairdresser.utils.AppUtils.FormatCurrency;
import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class OrderSummary extends AppCompatActivity implements View.OnClickListener {

    TextView priceSummary,city,phone,locationDescription,add_address,change_address;
    RecyclerView finalSummaryItems;
    MaterialButton payment;
    CartRepository cartRepository;
    AddressRepository addressRepository;
    CartAdapter cartAdapter;
    LinearLayout empty_address,existing_address;
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
        priceSummary =findViewById(R.id.priceSummary);
        city =findViewById(R.id.city);
        phone =findViewById(R.id.phone);
        finalSummaryItems =findViewById(R.id.finalSummaryItems);
        payment =findViewById(R.id.payment);
        add_address =findViewById(R.id.add_address);
        change_address =findViewById(R.id.change_address);
        locationDescription =findViewById(R.id.locationDescription);
        payment.setOnClickListener(this);
        add_address.setOnClickListener(this);
        change_address.setOnClickListener(this);
        cartRepository =new CartRepository(this);
        addressRepository =new AddressRepository(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.payment:
                break;
            case R.id.add_address:
                Intent i = new Intent(OrderSummary.this, AddressActivity.class);
                this.startActivityForResult(i, 1);
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
               break;
            case R.id.change_address:
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
//            if(resultCode == Activity.RESULT_CANCELED) {
//                displayToast(OrderSummary.this,false,"Delivery Address not selected");
//            }
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
        Integer total = 0;
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            total += cart.getPrice();
        }
        String pricing = getResources().getString(R.string.total_price) + FormatCurrency(total);
        String pricing1 = getResources().getString(R.string.payment_price) + FormatCurrency(total);
        priceSummary.setText(pricing);
        payment.setText(pricing1);
    }

    private void intAddresses(List<Address> addressList){
        for (int i = 0; i < addressList.size(); i++) {
            Address address = addressList.get(i);
            city.setText(address.getCity());
            locationDescription.setText(address.getDescription());
            phone.setText(address.getPhone());
        }
    }
}
