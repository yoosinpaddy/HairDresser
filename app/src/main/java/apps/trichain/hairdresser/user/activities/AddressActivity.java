package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    String _city,_description,_phone;
    MaterialButton save;
    AddressRepository addressRepository;
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
        save =findViewById(R.id.save);
        save.setOnClickListener(this);
        addressRepository =new AddressRepository(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                if (validatesInputs()){
                    addressRepository.getFullAddress().observe((LifecycleOwner) AddressActivity.this, addressList -> {
                        if (addressList.size() <=0 ){
                            addressRepository.insertAddress(_city,_description,_phone);
                            displayToast(AddressActivity.this,true,"Address Saved");
                        }if (addressList.size()>0){
                            Address address = new Address();
                            address.setCity(_city);
                            address.setDescription(_description);
                            address.setPhone(_phone);
                            addressRepository.updateAddress(address);
                            displayToast(AddressActivity.this,false,"Address Updated");
                        }
                    });
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                break;
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
