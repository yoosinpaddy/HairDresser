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
public class ConfirmCodeFragment extends Fragment implements  View.OnClickListener{

    public ConfirmCodeFragment() {
        // Required empty public constructor
    }
    private TextInputLayout confirm_codeLayout;
    private TextInputEditText confirm_code;
    private FloatingActionButton confirmcode;
    private TextView cancel;
    private ProgressBar progress;
    private OnFragmentInteractionListener mListener;
    private  Boolean isLoading =false;
    private String _code,_email;
    private ApiService apiService;
    Call<ResetResponse> resetResponseCall;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_confirm_code, container, false);
        confirm_codeLayout = view.findViewById(R.id.confirm_codeLayout);
        confirm_code = view.findViewById(R.id.confirm_code);
        confirmcode = view.findViewById(R.id.confirmcode);
        cancel = view.findViewById(R.id.cancel);
        progress = view.findViewById(R.id.progress);
        apiService = AppUtils.getApiService();
        confirmcode.setOnClickListener(this::onClick);
        cancel.setOnClickListener(this::onClick);
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
            mListener.onFragmentInteraction(extras, source );
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirmcode:
                if (validatesInputs()) {
                    if (NetworkUtils.getConnectivityStatus(Objects.requireNonNull(getActivity()))) {
                        confirmCode();
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
        _code = Objects.requireNonNull(confirm_code.getText()).toString().trim();

        if (_code.isEmpty()) {
            confirm_codeLayout.setError(getResources().getString(R.string.error_input));
            confirm_code.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void confirmCode() {
        Log.e(TAG, "confirmcode: ");
        disableUI(true);
        resetResponseCall = apiService.tokenconnfrm(_email,_code);
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
                            mListener.onFragmentInteraction(extras, 1 );

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
            confirm_code.setEnabled(false);
            cancel.setEnabled(false);
            hideView(confirmcode);
            showView(progress);
        } else {
            isLoading = false;
            confirm_code.setEnabled(true);
            cancel.setEnabled(true);

            hideView(progress);
            showView(confirmcode);
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
