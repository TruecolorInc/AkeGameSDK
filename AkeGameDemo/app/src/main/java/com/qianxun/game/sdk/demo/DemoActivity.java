package com.qianxun.game.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import media.ake.game.login.LoginManager;
import media.ake.game.login.OnLoginListener;
import media.ake.game.buy.OnBuyListener;
import media.ake.game.payment.PaymentManager;
import media.ake.game.query.OnQueryListener;
import media.ake.game.query.PaymentItem;

public class DemoActivity extends AppCompatActivity {

    private String mUserId;
    private List<PaymentItem> mItems;

    private void shownLogicBtn() {
        findViewById(R.id.query).setVisibility(View.VISIBLE);
        findViewById(R.id.pay).setVisibility(View.VISIBLE);
        findViewById(R.id.logout).setVisibility(View.VISIBLE);
        findViewById(R.id.login_by_google).setVisibility(View.GONE);
        findViewById(R.id.login_by_facebook).setVisibility(View.GONE);
        findViewById(R.id.login_by_app).setVisibility(View.GONE);
    }

    private void shownLogin() {
        findViewById(R.id.query).setVisibility(View.GONE);
        findViewById(R.id.pay).setVisibility(View.GONE);
        findViewById(R.id.logout).setVisibility(View.GONE);
        findViewById(R.id.login_by_google).setVisibility(View.VISIBLE);
        findViewById(R.id.login_by_facebook).setVisibility(View.VISIBLE);
        findViewById(R.id.login_by_app).setVisibility(View.VISIBLE);
    }

    private void clean() {
        mUserId = null;
        mItems = null;
        LoginManager.getInstance().logout(getApplicationContext());
        shownLogin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SignHelper.printFbSign(getApplication());
        setContentView(R.layout.activity_demo);
        clean();

        findViewById(R.id.login_by_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mUserId)) {
                    LoginManager.getInstance().loginByApp(DemoActivity.this, new OnLoginListener() {
                        @Override
                        public void onLoginSuccess(@NotNull String uid, long timestamp, @Nullable String sign) {
                            mUserId = uid;
                            Toast.makeText(DemoActivity.this, "login success: " + uid, Toast.LENGTH_SHORT).show();
                            shownLogicBtn();
                        }

                        @Override
                        public void onLoginFailed(String message) {
                            Toast.makeText(DemoActivity.this, "login failed: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        findViewById(R.id.login_by_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().loginByFaceBook(DemoActivity.this, new OnLoginListener() {
                    @Override
                    public void onLoginFailed(@Nullable String message) {
                        Toast.makeText(DemoActivity.this, "login failed: " + message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoginSuccess(@NotNull String uid, long timestamp, @Nullable String sign) {
                        mUserId = uid;
                        Toast.makeText(DemoActivity.this, "login success: " + uid, Toast.LENGTH_SHORT).show();
                        shownLogicBtn();
                    }
                });
            }
        });

        findViewById(R.id.login_by_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().loginByGoogle(DemoActivity.this, new OnLoginListener() {
                    @Override
                    public void onLoginFailed(@Nullable String message) {
                        Toast.makeText(DemoActivity.this, "login failed: " + message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoginSuccess(@NotNull String uid, long timestamp, @Nullable String sign) {
                        mUserId = uid;
                        Toast.makeText(DemoActivity.this, "login success: " + uid, Toast.LENGTH_SHORT).show();
                        shownLogicBtn();
                    }
                });
            }
        });

        findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });

        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemList();
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
            }
        });
    }

    private void query() {
        if(mUserId == null) {
            Toast.makeText(DemoActivity.this, "login first", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentManager.query(getApplicationContext(), mUserId, new OnQueryListener() {

            @Override
            public void onQuerySuccess(@NotNull List<PaymentItem> items) {
                mItems = items;
                Toast.makeText(DemoActivity.this, "query success: " + items.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onQueryFailed() {
                Toast.makeText(DemoActivity.this, "query failed", Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item);
        for(PaymentItem item : mItems) {
            arrayAdapter.add("Item " + item.name + " -- " + item.price);
        }
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pay(mItems.get(which));
            }
        });
        builderSingle.show();
    }

    private void pay(PaymentItem item) {
        if(TextUtils.isEmpty(mUserId)) {
            Toast.makeText(DemoActivity.this, "login first", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentManager.buy(getApplicationContext(), mUserId, item, new OnBuyListener() {
            @Override
            public void onPaySuccess() {
                Toast.makeText(DemoActivity.this, "pay success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPayFailed() {
                Toast.makeText(DemoActivity.this, "pay failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPayEnd() {
                Toast.makeText(DemoActivity.this, "pay ended", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoginManager.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
