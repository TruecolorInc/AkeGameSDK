package com.truecolor.splashexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qianxun.game.sdk.QianxunUtils;
import com.truecolor.ad.AdListener;
import com.truecolor.ad.AdSplashView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QianxunUtils.onCreate(this);
//        QianxunUtils.showQxSplash(this,R.layout.your_custom_splash_layout);
        QianxunUtils.showQxSplash(this,R.layout.your_transparenst_layout);
    }
}
