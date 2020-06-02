package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import apps.trichain.hairdresser.R;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar=findViewById(R.id.progress_horizontal);
        progressBar.setMax(100);
        progressBar.setProgress(20);
        final int[] i = {20};
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N&& i[0] >=100) {
                    progressBar.setProgress((i[0] +1),false);
                    progressBar.requestFocus();
                    progressBar.refreshDrawableState();
                    i[0] +=5;
                }
            }
        },40);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                finish();
            }
        },1200);
    }
}
