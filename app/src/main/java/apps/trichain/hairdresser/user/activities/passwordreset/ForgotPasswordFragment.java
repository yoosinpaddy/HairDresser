package apps.trichain.hairdresser.user.activities.passwordreset;

import androidx.core.app.SharedElementCallback;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.ResetResponse;
import apps.trichain.hairdresser.network.responses.UserResponse;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;


public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {


    private TextInputLayout emaillayout;
    private TextInputEditText email;
    private FloatingActionButton forgotPass;
    private TextView cancel;
    private OnFragmentInteractionListener mListener;
    private  Boolean isLoading =false;
    private ProgressBar progress;
    private String _email;
    private ApiService apiService;
    Call<ResetResponse> resetResponseCall;

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view= inflater.inflate(R.layout.forgot_password_fragment, container, false);
            emaillayout = view.findViewById(R.id.emaillayout);
            email = view.findViewById(R.id.email);
            forgotPass = view.findViewById(R.id.forgotPass);
            cancel = view.findViewById(R.id.cancel);
            progress = view.findViewById(R.id.progress);
            apiService = AppUtils.getApiService();

            forgotPass.setOnClickListener(this);
            cancel.setOnClickListener(this);


            return view;
        }


        // TODO: Rename method, update argument and hook method into UI event
        public void onButtonPressed(int source) {
            if (mListener != null) {
                Bundle extras = new Bundle();
                extras.putString("email", _email);
                mListener.onFragmentInteraction(extras, source );
            }
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.forgotPass:
                    if (validatesInputs()) {
                        if (NetworkUtils.getConnectivityStatus(Objects.requireNonNull(getActivity()))) {
                            forgotPassword();
                        }else{
                            displayToast(getActivity(),false,"No internet connection!");
                        }
                    }
                    break;
                case R.id.cancel:
                    if (isLoading) {
                        resetResponseCall.cancel();
                        displayToast(Objects.requireNonNull(getActivity()), false,"Request canceled!");
                    }
                    Objects.requireNonNull(getActivity()).finish();
                    break;
            }
        }

    public Boolean validatesInputs() {
        Log.e(TAG, "validatesInputs: ");
        _email = Objects.requireNonNull(email.getText()).toString().trim();

        if (_email.isEmpty()) {
            emaillayout.setError(getResources().getString(R.string.error_input));
            email.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void forgotPassword() {
        Log.e(TAG, "forgotpass: ");
        disableUI(true);
        resetResponseCall = apiService.forgotpassword(_email);
        resetResponseCall.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                disableUI(false);
                ResetResponse msg = response.body();
                if (response.isSuccessful()) {
                    Log.e("resesuccesscall", "......" + response.body());
                    if (msg != null) {
                        Log.e(TAG, "onResponse: "+msg.toString() );
                        if (!msg.getError()) {
                            AppUtils.displayToast(Objects.requireNonNull(getActivity()), true, msg.getMessage());
                            Bundle extras = new Bundle();
                            extras.putString("email", _email);
                            mListener.onFragmentInteraction(extras, 0 );

                        } else {
                            AppUtils.displayToast(Objects.requireNonNull(getActivity()), false, msg.getMessage());
                        }
                    }
                } else {
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(Objects.requireNonNull(getActivity()), false, "An error occurred. Please try Again");

                }
            }

            @Override
            public void onFailure(Call<ResetResponse> call, Throwable t) {
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
            cancel.setEnabled(false);
            hideView(forgotPass);
            showView(progress);
        } else {
            isLoading = false;
            email.setEnabled(true);
            cancel.setEnabled(true);

            hideView(progress);
            showView(forgotPass);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Bundle bundle,int source);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isLoading) {
            resetResponseCall.cancel();
        }
        mListener = null;
    }


}
