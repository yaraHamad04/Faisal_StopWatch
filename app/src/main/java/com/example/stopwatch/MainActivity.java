package com.example.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime;
    private Button btnStart, btnStop, btnReset;
    private Handler handler;
    private long startTime = 0L, timeInMilliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private Runnable updateTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);

        handler = new Handler();

        // Runnable to update the stopwatch time
        updateTimerThread = new Runnable() {
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds;

                int secs = (int) (updateTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                tvTime.setText(String.format("%02d:%02d", mins, secs));

                handler.postDelayed(this, 1000);
            }
        };

        // Start Button Click Event
        btnStart.setOnClickListener(v -> {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(updateTimerThread, 1000);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnReset.setEnabled(true);
        });

        // Stop Button Click Event
        btnStop.setOnClickListener(v -> {
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(updateTimerThread);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        });

        // Reset Button Click Event
        btnReset.setOnClickListener(v -> {
            startTime = 0L;
            timeSwapBuff = 0L;
            timeInMilliseconds = 0L;
            updateTime = 0L;
            tvTime.setText("00:00");
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnReset.setEnabled(false);
            handler.removeCallbacks(updateTimerThread);

        });
    }
}
