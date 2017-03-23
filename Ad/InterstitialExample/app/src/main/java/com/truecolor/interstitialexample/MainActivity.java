package com.truecolor.interstitialexample;

import android.*;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.qianxun.game.sdk.QianxunUtils;
import com.truecolor.ad.AdConfigure;
import com.truecolor.ad.AdInterstitialView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final long GAME_LENGTH_MILLISECONDS = 3000;

    private CountDownTimer mCountDownTimer;
    private Button mRetryButton;
    private boolean mGameIsInProgress;
    private long mTimerMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize  QianxunUtils
        QianxunUtils.onCreate(this);

        QianxunUtils.initAdInterstitial(this, new com.truecolor.ad.AdListener() {

            @Override
            public void onReceiveAdFailed(int arg0, int arg1) {

            }

            @Override
            public void onReceiveAd(int arg0) {
                Log.i(TAG, "onReceiveAd = " + arg0);
            }

            @Override
            public void onAdShow(int arg0) {
                Log.i(TAG, "onAdShow = " + arg0);
            }

            /**
             * Called when close Ad. Do your own logic。
             * @param arg0
             */
            @Override
            public void onAdDismiss(int arg0) {
                Log.i(TAG, "onAdDismiss = " + arg0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                });
            }

            @Override
            public void onAdClick(int arg0) {
                Log.i(TAG, "onAdClick = " + arg0);
            }

            @Override
            public void onAdAction(String arg0) {
                Log.i(TAG, "onAdAction = " + arg0);
            }
        });

        // Create the "retry" button, which tries to show an interstitial between game plays.
        mRetryButton = ((Button)findViewById(R.id.retry_button));
        mRetryButton.setVisibility(View.INVISIBLE);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

        startGame();

        if(!checkPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setMessage("为了在Android M上正常播放广告,需要您的授权.")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showPermissionDialog(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        }
    }

    private void createTimer(final long milliseconds) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        final TextView textView = ((TextView)findViewById(R.id.timer));

        mCountDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                mTimerMilliseconds = millisUnitFinished;
                textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                mGameIsInProgress = false;
                textView.setText("done!");
                mRetryButton.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();

        if(mGameIsInProgress) {
            resumeGame(mTimerMilliseconds);
        }
    }

    @Override
    public void onPause() {
        // Cancel the timer if the game is paused.
        mCountDownTimer.cancel();
        super.onPause();
    }

    private void showInterstitial() {
//         Show the ad if it's ready. Otherwise toast and restart the game.
        boolean isShow = QianxunUtils.showAdInterstitial();
        if(isShow) {
            Toast.makeText(this, "广告展示成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startGame();
        }
    }

    private void startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        mRetryButton.setVisibility(View.INVISIBLE);
        resumeGame(GAME_LENGTH_MILLISECONDS);
    }

    private void resumeGame(long milliseconds) {
        // Create a new timer for the correct length and start it.
        mGameIsInProgress = true;
        mTimerMilliseconds = milliseconds;
        createTimer(milliseconds);
        mCountDownTimer.start();
    }

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;

    public static boolean checkPermission(final Activity activity, final String permission) {
        if(Build.VERSION.SDK_INT >= 23) {
            int storagePermission = ActivityCompat.checkSelfPermission(activity, permission);
            if(storagePermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showPermissionDialog(final Activity activity, String permission) {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE_ASK_PERMISSIONS);
    }
}
