package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.UserResponse;
import apps.trichain.hairdresser.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText email;
    private TextInputEditText password;
    private MaterialButton loginbutton;
    private TextView createAccount;
    private TextInputLayout usernamelayout, passlayout;
    ImageView logo, logo2;
    private String _email, _password;
    private boolean isLoading = false;
    private static final String TAG = "Login";
    private ApiService apiService;
    Call<UserResponse> userResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUi();
        logo = findViewById(R.id.imageView);
    }

    private void initUi() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        usernamelayout = findViewById(R.id.usernamelayout);
        passlayout = findViewById(R.id.passlayout);
        loginbutton = findViewById(R.id.loginbutton);
        createAccount = findViewById(R.id.createAccount);

        apiService = AppUtils.getApiService();
        loginbutton.setOnClickListener(this);
    }


    public Boolean validatesInputs() {
        Log.e(TAG, "validatesInputs: ");
        _email = Objects.requireNonNull(email.getText()).toString().trim();
        _password = Objects.requireNonNull(password.getText()).toString().trim();

        if (_email.isEmpty()) {
            usernamelayout.setError(getResources().getString(R.string.error_input));
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
            case R.id.loginbutton:
                if (validatesInputs()) {
                    loginUser();
                }
            case R.id.forgot_password:
                if (validatesInputs()) {
                    loginUser();
                }

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
                            AppUtils.displayToast(Login.this, true, msg.getMessage());
                        } else {
                            AppUtils.displayToast(Login.this, false, msg.getMessage());
                        }
                    }
                } else {
                    AppUtils.displayToast(Login.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(Login.this, false, "Please try Again");

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
            Login.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            hideView(loginbutton);
//            showView(progress);
        } else {
            isLoading = false;
            Login.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            hideView(progress);
            showView(loginbutton);
        }


    }
}
