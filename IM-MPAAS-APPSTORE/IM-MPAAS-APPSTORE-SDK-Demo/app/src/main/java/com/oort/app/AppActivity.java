package com.oort.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oort.core.constant.Constant;
import com.oort.weichat.ui.MainActivity;
import com.oortcloud.appstore.AppStoreActivity;

/**
 * Email 465571041@qq.com
 * Created by zhang-zhi-jun on 2026/2/6-15:50.
 * Version 1.0
 * Description:
 */
public class AppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_activity);

        findViewById(R.id.im_app_store).setOnClickListener(view -> {
            Constant.openActivity = 3;
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

    }

}
