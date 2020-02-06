package it.agevoluzione.tools.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public abstract class CloserActivity extends AppCompatActivity {

    private BroadcastReceiver closer;

    @NonNull
    public  BroadcastReceiver closerForActivity() {
        final String action = "act.close."+ getLocalClassName();
        BroadcastReceiver closer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AndroidUtils.checkActionFromIntent(intent, action)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else {
                        finish();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(action);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(closer, filter);
        return closer;
    }

    @Override
    final protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closer = closerForActivity();
        onCreate2(savedInstanceState);
    }

    @Override
    final protected void onDestroy() {
        super.onDestroy();
        if (null != closer) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(closer);
        }
        onDestroy2();
    }

    protected void onCreate2(@Nullable Bundle savedInstanceState){
    }

    protected void onDestroy2() {
    }

    public static void close(String getLocalClassName, Context context) {
        String action = "act.close."+ getLocalClassName;
        Intent intentClose =  new Intent(action);
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .sendBroadcast(intentClose);
    }
}
