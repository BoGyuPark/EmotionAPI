package com.example.shain.emotionapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);

        Button voicebtn = (Button) findViewById(R.id.voice_btn);
        Button readbtn = (Button) findViewById(R.id.read_btn);
        Button recordbtn = (Button) findViewById(R.id.record_btn);
        Button backbtn=(Button)findViewById(R.id.button2);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        voicebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voiceintent = new Intent(getApplicationContext(), VoiceActivity.class);
                voiceintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                editor = prefs.edit();
                editor.putInt("PageNum",0);

                editor.putString("Emotion_1","default");
                editor.putString("Emotion_2","default");
                editor.putString("Emotion_3","default");

                editor.apply();
                startActivity(voiceintent);
            }
        });
        readbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent readintent = new Intent(getApplicationContext(), ReadActivity.class);
                readintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                editor = prefs.edit();
                editor.putInt("PageNum",0);

                editor.putString("Emotion_1","default");
                editor.putString("Emotion_2","default");
                editor.putString("Emotion_3","default");

                editor.apply();
                startActivity(readintent);
            }
        });
        recordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordinsert = new Intent(getApplicationContext(), RecordActivity.class);
                recordinsert.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                editor = prefs.edit();
                editor.putInt("PageNum",0);

                editor.putString("Emotion_1","default");
                editor.putString("Emotion_2","default");
                editor.putString("Emotion_3","default");

                editor.apply();
                startActivity(recordinsert);
            }
        });
    }
}
