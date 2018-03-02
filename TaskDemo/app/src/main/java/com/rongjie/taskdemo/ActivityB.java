package com.rongjie.taskdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class ActivityB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(getClass().getName(),"onresume b ......");
    }

    public void toC(View view){
        Intent intent = new Intent(this,ActivityC.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getName(),"on destroy b......");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(getClass().getName(),"on new intent b ......");
    }
}
