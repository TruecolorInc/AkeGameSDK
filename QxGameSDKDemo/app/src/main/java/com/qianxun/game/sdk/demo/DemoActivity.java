package com.qianxun.game.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.qianxun.game.sdk.OnLoginListener;
import com.qianxun.game.sdk.OnLogoutListener;
import com.qianxun.game.sdk.OnModifyPasswordListener;
import com.qianxun.game.sdk.OnPayListener;
import com.qianxun.game.sdk.OnQueryListener;
import com.qianxun.game.sdk.PayItem;
import com.qianxun.game.sdk.QianxunUtils;
import com.qianxun.game.sdk.UserInfo;

import androidx.appcompat.app.AppCompatActivity;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QianxunUtils.onCreate(DemoActivity.this);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });

        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItemList();
            }
        });

        findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void login() {
        QianxunUtils.login(DemoActivity.this, new OnLoginListener() {

            /**
             *
             * @param userInfo 用户信息
             * @param timestamp 时间戳
             * @param sign 验证用
             */
            @Override
            public void onLoginSuccess(UserInfo userInfo, long timestamp, String sign) {
                Toast.makeText(DemoActivity.this, "login success: userInfo.gameId=" + userInfo.gameId
                        + "--userInfo.qianXunId=" + userInfo.qianXunId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginFailed(String msg) {
                Toast.makeText(DemoActivity.this, "login failed," + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void share() {
        QianxunUtils.share(DemoActivity.this, "title", "详情", "https://play.goole.com/store/apps/details?id=com.boyaa.lordland.zongcai",
                "https://lh3.googleusercontent.com/htT6aHmNx69l2nPQm6jfzjkJgLRg1bGMsts2yNvq_2xzGtOugxgwKD33j5Zd9pK0iv0=h310-rw", new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(DemoActivity.this, "share success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(DemoActivity.this, "share onCancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(DemoActivity.this, "share onError," + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private PayItem[] mItems;

    private void query() {
        QianxunUtils.query(DemoActivity.this, new OnQueryListener() {
            @Override
            public void onQuerySuccess(PayItem[] payItems) {
                mItems = payItems;
                Toast.makeText(DemoActivity.this, "query success: " + payItems.length, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onQueryFailed(String msg) {
                Toast.makeText(DemoActivity.this, "query failed," + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showItemList() {
        if(mItems == null) {
            Toast.makeText(DemoActivity.this, "query first", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Item");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.select_dialog_item);
        for(PayItem item : mItems) {
            arrayAdapter.add("Item " + item.item_id + " -- " + "Name:" + item.name + "--" + item.price);
        }
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pay(mItems[which].item_id);
            }
        });
        builderSingle.show();
    }

    private void pay(int itemId) {
        QianxunUtils.pay(DemoActivity.this, itemId, "extra_data", "call_back", new OnPayListener() {
            @Override
            public void onPaySuccess(int itemId, String extraData) {
                Toast.makeText(DemoActivity.this, "pay success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPayPending(int itemId, String extraData) {
                Toast.makeText(DemoActivity.this, "pay pending", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPayFailed(int itemId, String extraData, String message) {
                Toast.makeText(DemoActivity.this, "pay failed," + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        QianxunUtils.changePassword(DemoActivity.this, new OnModifyPasswordListener() {
            @Override
            public void onModifySuccess() {
                Toast.makeText(DemoActivity.this, "reset success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onModifyFailed(String message) {
                Toast.makeText(DemoActivity.this, "reset fail," + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        QianxunUtils.logout(DemoActivity.this, new OnLogoutListener() {
            @Override
            public void onLogoutSuccess() {
                Toast.makeText(getApplicationContext(), "logout Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutFailed() {
                Toast.makeText(getApplicationContext(), "logout Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        QianxunUtils.onStart(DemoActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QianxunUtils.onResume(DemoActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        QianxunUtils.onStop(DemoActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        QianxunUtils.onPause(DemoActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QianxunUtils.onDestroy(DemoActivity.this);
    }
}
