package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import apps.trichain.hairdresser.R;

public class LoginActivity extends AppCompatActivity {

    ImageView logo,logo2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logo=findViewById(R.id.imageView);
    }
}
