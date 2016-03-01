package com.bsupits.persistentservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static String mBroadcastStringAction = "com.bsupits.persistentservice.TIMER";
    @Bind(R.id.counter) TextView textView;
    @Bind(R.id.btn_start) Button btnStart;
    @Bind(R.id.btn_stop) Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide button start
                btnStart.setVisibility(View.INVISIBLE);

                // show button stop
                btnStop.setVisibility(View.VISIBLE);

                // start Persistent Service
                Intent intent = new Intent(MainActivity.this,PersistentService.class);
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnStop();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(mBroadcastStringAction));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onDestroy();
    }

    private void OnStop() {
        // stop Persistent Service
        stopService(new Intent(MainActivity.this, PersistentService.class));

        // hide button stop
        btnStop.setVisibility(View.INVISIBLE);

        // show button start
        btnStart.setVisibility(View.VISIBLE);

        // clear textView
        textView.setText("");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                textView.setText(intent.getStringExtra("second"));
                if (intent.getStringExtra("second").equalsIgnoreCase("00")) {
                    CountDownTimer countDownTimer = new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {}

                        @Override
                        public void onFinish() {
                            OnStop();
                        }
                    }.start();
                }
            }
        }
    };

}
