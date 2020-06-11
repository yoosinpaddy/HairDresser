package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.storage.repositories.AddressRepository;
import apps.trichain.hairdresser.user.models.Address;
import apps.trichain.hairdresser.utils.AppUtils;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddressActivity";
    TextInputEditText city,description,phone;
    TextInputLayout citylayout,descriptionlayout,phonelayout;
    ImageView addres_back;
    String _city;
    String _description;
    String _phone;
    int addressID;
    MaterialButton save;
    AddressRepository addressRepository;
    private boolean isNew =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initUi();
    }

    private  void initUi(){
        city =findViewById(R.id.city);
        citylayout =findViewById(R.id.citylayout);
        description =findViewById(R.id.description);
        descriptionlayout =findViewById(R.id.descriptionlayout);
        phone =findViewById(R.id.phone);
        phonelayout =findViewById(R.id.phonelayout);
        addres_back =findViewById(R.id.addres_back);
        save =findViewById(R.id.save);
        save.setOnClickListener(this);
        addres_back.setOnClickListener(this);
        addressRepository =new AddressRepository(this);

        addressRepository.getFullAddress().observe(this, addressList -> {
            assert addressList != null;
            if(addressList.size() > 0) {
                intAddresses(addressList);
                isNew=false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                if (validatesInputs()){
                    if (isNew){
                        addressRepository.getFullAddress().observe((LifecycleOwner) AddressActivity.this, addressList -> {
                            if (addressList.size() <=0 ){
                                addressRepository.insertAddress(_city,_description,_phone);
                                displayToast(AddressActivity.this,true,"Address Saved");
                            }
                        });
                    }else {
                        addressRepository.updateAddress(addressID,_city,_description,_phone);
                        displayToast(AddressActivity.this,true,"Address Updated");
                    }

                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                break;
            case R.id.addres_back:
                onBackPressed();
                break;
        }
    }

    private void intAddresses(List<Address> addressList){
        for (int i = 0; i < addressList.size(); i++) {
            Address address = addressList.get(i);
            city.setText(address.getCity());
            description.setText(address.getDescription());
            phone.setText(address.getPhone());
            addressID =address.getId();
        }
    }


    public Boolean validatesInputs() {
        Log.e(TAG, "validatesInputs: ");
        _city = Objects.requireNonNull(city.getText()).toString().trim();
        _description = Objects.requireNonNull(description.getText()).toString().trim();
        _phone = Objects.requireNonNull(phone.getText()).toString().trim();

        if (_city.isEmpty()) {
            citylayout.setError(getResources().getString(R.string.error_input));
            city.requestFocus();
            return false;
        } else if (_description.isEmpty()) {
            descriptionlayout.setError(getResources().getString(R.string.error_input));
            descriptionlayout.requestFocus();
            return false;
        }else if (_phone.isEmpty()) {
            phonelayout.setError(getResources().getString(R.string.error_input));
            phone.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
