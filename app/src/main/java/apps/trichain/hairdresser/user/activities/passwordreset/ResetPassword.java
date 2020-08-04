package apps.trichain.hairdresser.user.activities.passwordreset;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.user.activities.LoginActivity;

public class ResetPassword extends AppCompatActivity  implements  ForgotPasswordFragment.OnFragmentInteractionListener,
        ConfirmCodeFragment.OnFragmentInteractionListener, UpdatePasswordFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        if (savedInstanceState == null) {
            Bundle extras = new Bundle();
            extras.putString("data", "data");
            loadFragment(new ForgotPasswordFragment(),extras);
        }
    }

    public void loadFragment(Fragment fragment, Bundle bundle) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Bundle bundle,int source) {
        switch (source){
            case 0:
                loadFragment(new ConfirmCodeFragment(),bundle);
                break;
            case 1:
                loadFragment(new UpdatePasswordFragment(),bundle);
                break;
            case 2:
                startActivity(new Intent(ResetPassword.this, LoginActivity.class));
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                finish();
                break;
        }
    }
}
