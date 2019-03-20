package com.example.liuyang.gettimestamp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by LiuYang on 2019/1/22.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 判断是否有SYSTEM_ALERT_WINDOW权限
            if(!Settings.canDrawOverlays(this)) {
                // 申请SYSTEM_ALERT_WINDOW权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                try{
                    startActivityForResult(intent, 102);
                }
                catch(Exception e)
                {
                    // 有的定制系统会抛异常，这样的系统也不需要额外的悬浮窗授权
                }
            }else{
                startService(new Intent(MainActivity.this, FloatView.class));
                finish();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(MainActivity.this, FloatView.class));
    }
}

