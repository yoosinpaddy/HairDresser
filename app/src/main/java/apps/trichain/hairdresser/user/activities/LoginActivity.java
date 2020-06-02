package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText email;
    private TextInputEditText password;
    private MaterialButton loginbutton;
    private TextView createAccount;
    private TextInputLayout passwordlayout, passlayout;
    ProgressBar progress;
    ImageView logo, logo2;
    private String _email, _password;
    private boolean isLoading = false;
    private static final String TAG = "Login";
    private ApiService apiService;
    Call<UserResponse> userResponseCall;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (SharedPrefManager.getInstance(this).isLogin()) {
            startActivity(new Intent(LoginActivity.this, ServicesActivity.class));
            overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
            finish();

        }
        initUi();
        logo = findViewById(R.id.imageView);
    }

    private void initUi() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordlayout = findViewById(R.id.emaillayout);
        passlayout = findViewById(R.id.passwordlayout);
        loginbutton = findViewById(R.id.login);
        progress = findViewById(R.id.progress);
        createAccount = findViewById(R.id.createAccount);

        apiService = AppUtils.getApiService();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        loginbutton.setOnClickListener(this);
        createAccount.setOnClickListener(this);
    }



    public Boolean validatesInputs() {
        Log.e(TAG, "validatesInputs: ");
        _email = Objects.requireNonNull(email.getText()).toString().trim();
        _password = Objects.requireNonNull(password.getText()).toString().trim();

        if (_email.isEmpty()) {
            passwordlayout.setError(getResources().getString(R.string.error_input));
            email.requestFocus();
            return false;
        } else if (_password.isEmpty()) {
            passlayout.setError(getResources().getString(R.string.error_input));
            password.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (validatesInputs()) {
                    if (NetworkUtils.getConnectivityStatus(LoginActivity.this)) {
                        loginUser();
                    }else{
                        displayToast(LoginActivity.this,false,"No internet connection!");
                    }
                }
                break;
            case R.id.createAccount:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                break;

        }
    }

    public void loginUser() {
        Log.e(TAG, "loginUser: ");
        disableUI(true);
        userResponseCall = apiService.userLogin(_email, _password);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                disableUI(false);
                UserResponse msg = response.body();
                if (response.isSuccessful()) {
                    Log.e("successlogin", "......" + response);
                    if (msg != null) {
                        if (!msg.getError()) {
                            AppUtils.displayToast(LoginActivity.this, true, msg.getMessage());
                            sharedPrefManager.customerLogin(
                                    true,
                                    msg.getUser()
                            );
                            startActivity(new Intent(LoginActivity.this, ServicesActivity.class));
                            overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                            finish();

                        } else {
                            AppUtils.displayToast(LoginActivity.this, false, msg.getMessage());
                        }
                    }
                } else {
                    AppUtils.displayToast(LoginActivity.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(LoginActivity.this, false, "Please try Again");

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
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
            isLoading = true;
            email.setEnabled(false);
            password.setEnabled(false);
            hideView(loginbutton);
            showView(progress);
        } else {
            isLoading = false;
            email.setEnabled(true);
            password.setEnabled(true);
            hideView(progress);
            showView(loginbutton);
        }


    }
}
