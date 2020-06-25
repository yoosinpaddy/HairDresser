package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.user.adapters.OrdersAdapter;

public class RatingActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener  {

    private SeekBar seekBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        final RatingBar ratingbar = findViewById(R.id.ratingbar);
        ratingbar.setOnRatingBarChangeListener(this);
        // Initialize the textview with '0'
        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progressValue, boolean fromUser) {
                        progress = progressValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textView.setText(progress + "/" + seekBar.getMax());
                    }
                });
    }
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating,
                                boolean fromUser) {
        Toast.makeText(RatingActivity.this, "New Rating: " + rating,
                Toast.LENGTH_SHORT).show();
    }


}