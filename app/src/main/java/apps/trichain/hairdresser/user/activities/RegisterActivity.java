package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.UserResponse;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener {
    private TextInputLayout fnameh;
    private TextInputEditText fname;
    private TextInputLayout snameh;
    private TextInputEditText sname;
    private TextInputLayout emailh;
    private TextInputEditText email;
    private TextInputLayout passwordh;
    private TextInputEditText password;
    private TextInputLayout passwordrh;
    private TextInputEditText passwordr;
    private TextInputLayout phoneh;
    private TextInputEditText phone;
    private TextInputLayout cityh;
    private TextInputEditText city;
    private MaterialButton register;
    private TextView login;
    private ProgressBar progress;
    private String _fname,_sname,_phone,_city,_email,_password,_cpassword;
    private ApiService apiService;
    private boolean isLoading=false;
    Call<UserResponse> userResponseCall;
    private static final String TAG = "Register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUi();
    }


    private void initUi() {
        fnameh = (TextInputLayout) findViewById(R.id.fnameh);
        fname = (TextInputEditText) findViewById(R.id.fname);
        snameh = (TextInputLayout) findViewById(R.id.passwordlayout);
        sname = (TextInputEditText) findViewById(R.id.sname);
        emailh = (TextInputLayout) findViewById(R.id.emailh);
        email = (TextInputEditText) findViewById(R.id.email);
        passwordh = (TextInputLayout) findViewById(R.id.passwordh);
        password = (TextInputEditText) findViewById(R.id.password);
        passwordrh = (TextInputLayout) findViewById(R.id.passwordrh);
        passwordr = (TextInputEditText) findViewById(R.id.passwordr);
        phoneh = (TextInputLayout) findViewById(R.id.phoneh);
        phone = (TextInputEditText) findViewById(R.id.phone);
        cityh = (TextInputLayout) findViewById(R.id.emaillayout);
        city = (TextInputEditText) findViewById(R.id.city);
        register = (MaterialButton) findViewById(R.id.register);
        login = (TextView) findViewById(R.id.login);
        progress = findViewById(R.id.progress);
        apiService = AppUtils.getApiService();
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                if (validatesInputs()){
                    if (NetworkUtils.getConnectivityStatus(RegisterActivity.this)){
                        registerUser();
                    }else {
                        displayToast(RegisterActivity.this,false,"No internet connection!");
                    }

                }
                break;
            case R.id.login:
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                break;
        }
    }

    public Boolean validatesInputs(){
        _fname =  Objects.requireNonNull(fname.getText()).toString().trim();
        _sname =  Objects.requireNonNull(sname.getText()).toString().trim();
        _phone =  Objects.requireNonNull(phone.getText()).toString().trim();
        _city =  Objects.requireNonNull(city.getText()).toString().trim();
        _password =  Objects.requireNonNull(password.getText()).toString().trim();
        _cpassword =  Objects.requireNonNull(passwordr.getText()).toString().trim();
        _email =  Objects.requireNonNull(email.getText()).toString().trim();

        if (_fname.isEmpty()){
            fnameh.setError(getResources().getString(R.string.error_input));
            fname.requestFocus();
            return false;
        }else if (_sname.isEmpty()) {
            snameh.setError(getResources().getString(R.string.error_input));
            sname.requestFocus();
            return false;
        }else if (_phone.isEmpty()) {
            phoneh.setError(getResources().getString(R.string.error_input));
            phone.requestFocus();
            return false;
        }else if (_city.isEmpty()) {
            cityh.setError(getResources().getString(R.string.error_input));
            city.requestFocus();
            return false;
        }else if (_password.isEmpty()) {
            passwordh.setError(getResources().getString(R.string.error_input));
            password.requestFocus();
            return false;
        }else if (_cpassword.isEmpty()) {
            passwordrh.setError(getResources().getString(R.string.error_input));
            passwordr.requestFocus();
            return false;
        }else if(!_password.equals(_cpassword)){
            passwordrh.setError(getResources().getString(R.string.pass_error_input));
            passwordr.requestFocus();
            return false;
        }else if (_email.isEmpty()) {
            emailh.setError(getResources().getString(R.string.error_input));
            email.requestFocus();
            return false;
        }else {
            return true;
        }
    }

    public void registerUser(){

        disableUI(true);
        userResponseCall = apiService.userRegister(_fname,_sname,_email,_phone,_city,"client",_password);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                disableUI(false);
                UserResponse msg=response.body();
                if (response.isSuccessful()) {
                    Log.e("succesbody: ", "----- "+response);
                    if (msg!=null){
                        if(!msg.getError()){
                            AppUtils.displayToast(RegisterActivity.this,true, msg.getMessage());
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                            finish();

                        }else {
                            AppUtils.displayToast(RegisterActivity.this,false,msg.getMessage());
                        }
                    }else{
                        AppUtils.displayToast(RegisterActivity.this,false,"something bad happened!");
                    }
                } else {
                    Log.e("errorbody: ", "----- "+response);
                    AppUtils.displayToast(RegisterActivity.this,false,"Please try again!!");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                disableUI(false);
                AppUtils.displayToast(RegisterActivity.this,false,"Failed to register!!");
                Log.e(TAG, "onFailure: "+call.request().url().toString() );
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: Canceled! " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailure: Failed! " + t.getLocalizedMessage());
                }
            }
        });
    }

    private void disableUI(Boolean value){
        if (value){
            isLoading=true;
            fname.setEnabled(false);
            sname.setEnabled(false);
            email.setEnabled(false);
            password.setEnabled(false);
            passwordr.setEnabled(false);
            phone.setEnabled(false);
            city.setEnabled(false);
            hideView(register);
            showView(progress);
        }
        else {
            isLoading=false;
            fname.setEnabled(true);
            sname.setEnabled(true);
            email.setEnabled(true);
            password.setEnabled(true);
            passwordr.setEnabled(true);
            phone.setEnabled(true);
            city.setEnabled(true);
            hideView(progress);
            showView(register);
        }


    }
}
