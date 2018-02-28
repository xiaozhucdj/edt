package com.rongjie.taskdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ActivityC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainc);
    }

    public void toD(View view){
        Intent intent = new Intent(this,ActivityD.class);
        startActivity(intent);
    }
}
