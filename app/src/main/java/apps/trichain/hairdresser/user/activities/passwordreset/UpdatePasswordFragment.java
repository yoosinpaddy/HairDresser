package apps.trichain.hairdresser.user.activities.passwordreset;

import android.content.Context;
import android.os.Bundle;

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
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatePasswordFragment extends Fragment implements  View.OnClickListener{

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    private TextInputLayout newpasslayout,confirmpasslayout;
    private TextInputEditText newpass,confirmpassword;
    private FloatingActionButton updatePass;
    private TextView cancel;
    private ProgressBar progress;
    private OnFragmentInteractionListener mListener;
    private  Boolean isLoading =false;
    private String _email,_password,c_password;
    private ApiService apiService;
    Call<ResetResponse> resetResponseCall;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        newpass = view.findViewById(R.id.newpass);
        confirmpassword = view.findViewById(R.id.confirmpassword);
        newpasslayout = view.findViewById(R.id.newpasslayout);
        confirmpasslayout = view.findViewById(R.id.confirmpasslayout);
        cancel = view.findViewById(R.id.cancel);
        progress = view.findViewById(R.id.progress);
        updatePass = view.findViewById(R.id.updatePass);
        apiService = AppUtils.getApiService();
        updatePass.setOnClickListener(this);
        cancel.setOnClickListener(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _email = bundle.getString("email", null);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int source) {
        if (mListener != null) {
            Bundle extras = new Bundle();
            extras.putString("email", _email);
            mListener.onFragmentInteraction( extras,source );
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updatePass:
                if (validatesInputs()) {
                    if (NetworkUtils.getConnectivityStatus(Objects.requireNonNull(getActivity()))) {
                        updatePassword();
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
        _password = Objects.requireNonNull(newpass.getText()).toString().trim();
        c_password = Objects.requireNonNull(confirmpassword.getText()).toString().trim();

        if (_password.isEmpty()) {
            newpasslayout.setError(getResources().getString(R.string.error_input));
            newpass.requestFocus();
            return false;
        }else if(!_password.equals(c_password)){
            confirmpasslayout.setError(getResources().getString(R.string.pass_error_input));
            confirmpassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void updatePassword() {
        Log.e(TAG, "updatepass: ");
        disableUI(true);
        resetResponseCall = apiService.changePassword(_email,_password);
        resetResponseCall.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                disableUI(false);
                ResetResponse msg = response.body();
                if (response.isSuccessful()) {
                    if (msg != null) {
                        Log.e(TAG, "onResponse: "+msg.toString() );
                        if (!msg.getError()) {
                            AppUtils.displayToast(Objects.requireNonNull(getActivity()), true, msg.getMessage());
                            Bundle extras = new Bundle();
                            extras.putString("email", _email);
                            mListener.onFragmentInteraction( extras,2 );

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
            newpass.setEnabled(false);
            confirmpassword.setEnabled(false);
            cancel.setEnabled(false);
            hideView(updatePass);
            showView(progress);
        } else {
            isLoading = false;
            newpass.setEnabled(true);
            confirmpassword.setEnabled(true);
            cancel.setEnabled(true);
            hideView(progress);
            showView(updatePass);
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
