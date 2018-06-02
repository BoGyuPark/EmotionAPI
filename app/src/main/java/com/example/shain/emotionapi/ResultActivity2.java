package com.example.shain.emotionapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ResultActivity2 extends AppCompatActivity{
    Button reRecord,selectBook2;
    SharedPreferences prefs = null;
    SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_result2);

        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        editor = prefs.edit();


        reRecord = (Button) findViewById(R.id.reRecord);
        selectBook2 = (Button) findViewById(R.id.selectBook2);

        reRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResultActivity2.this,RecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.putInt("PageNum",0);
                editor.apply();
                startActivity(intent);
            }
        });
        selectBook2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(ResultActivity2.this,MainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        });
    }
}
