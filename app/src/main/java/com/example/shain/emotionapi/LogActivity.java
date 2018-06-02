package com.example.shain.emotionapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class LogActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        editor = prefs.edit();

        Button back;
        ScrollView sv;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_log, null);
        setContentView(v);

        sv = (ScrollView) v.findViewById(R.id.scrollview);
        back = (Button) findViewById(R.id.back);

        // Create a LinearLayout element
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        // Add text


        String[] tx = getResources().getStringArray(R.array.script);
        TextView[] tv = new TextView[tx.length];

        for (int i = 0; i < tx.length; ++i) {
            tv[i] = new TextView(this);
            tv[i].setText(tx[i]);
            tv[i].setPadding(0, 40, 200, 40);

            final int index = i;
            tv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int layout = prefs.getInt("Layout", 0);
                    editor.putInt("PageNum", index);
                    editor.apply();

                    Intent intent = null;
                    if (layout == 0)
                        intent = new Intent(LogActivity.this, ReadActivity.class);
                    else if (layout == 1)
                        intent = new Intent(LogActivity.this, VoiceActivity.class);
                    else
                        intent = new Intent(LogActivity.this, RecordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    //finish();
                }
            });

            ll.addView(tv[i]);
        }
        // Add the LinearLayout element to the ScrollView
        sv.addView(ll);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}