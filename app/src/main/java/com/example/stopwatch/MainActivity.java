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
    private boolean isRunning = false;


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
                updateDisplay();
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        };

        // Start Button Click Event
        btnStart.setOnClickListener(v -> {
            startTime = SystemClock.uptimeMillis();
            handler.post(updateTimerThread);
            isRunning = true;
            updateButtonStates(false, true, true);
        });

        // Stop Button Click Event
        btnStop.setOnClickListener(v -> {
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(updateTimerThread);
            isRunning = true;
            updateButtonStates(true, false, true);
        });

        // Reset Button Click Event
        btnReset.setOnClickListener(v -> {
            startTime = timeSwapBuff = timeInMilliseconds = updateTime = 0L;
            updateDisplay();
            handler.removeCallbacks(updateTimerThread);
            isRunning = true;
            updateButtonStates(true, false, false);
        });

        if (savedInstanceState != null) {
            updateTime = savedInstanceState.getLong("updateTime", 0L);
            timeSwapBuff = savedInstanceState.getLong("timeSwapBuff", 0L);
            isRunning = savedInstanceState.getBoolean("isRunning", false);

            updateDisplay();

            if (isRunning) {
                startTime = SystemClock.uptimeMillis();
                updateButtonStates(true, false, true);
            }
        }

    }

    // Helper method to update the displayed time
    private void updateDisplay() {
        int secs = (int) (updateTime / 1000);
        int mins = secs / 60;
        secs %= 60;
        tvTime.setText(String.format("%02d:%02d", mins, secs));
    }

    // Helper method to enable/disable buttons
    private void updateButtonStates(boolean start, boolean stop, boolean reset) {
        btnStart.setEnabled(start);
        btnStop.setEnabled(stop);
        btnReset.setEnabled(reset);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("updateTime", updateTime);
        outState.putLong("timeSwapBuff", timeSwapBuff);
        outState.putBoolean("isRunning", isRunning);
    }

}
