package com.ss.sssdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class testActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        startService(new Intent(testActivity.this,PushMessageService.class));
    }


}
