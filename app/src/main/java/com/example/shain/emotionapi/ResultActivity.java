package com.example.shain.emotionapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    RatingBar rating;
    TextView rateText;
    Button reRead,selectBook;
    SharedPreferences prefs = null;
    SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_result);

        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        editor = prefs.edit();

        rating=(RatingBar)findViewById(R.id.rating);
        rateText=(TextView)findViewById(R.id.rateText);
        reRead=(Button)findViewById(R.id.reRead);
        selectBook=(Button)findViewById(R.id.selectBook);


        int rate=prefs.getInt("point",0);

        rating.setRating((float)rate);
        switch (rate){
            case 0:
                rateText.setText("다시 도전해보세요");
            case 1:
                rateText.setText("아쉬워요ㅠㅠ");
            case 2:
                rateText.setText("좋아요!");
            default:
                rateText.setText("참 잘했어요!");
        }

        reRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=null;
                int layout=prefs.getInt("Layout",0);
                if(layout==0){
                    intent=new Intent(ResultActivity.this,ReadActivity.class);
                }
                else if(layout==1){
                    intent=new Intent(ResultActivity.this,VoiceActivity.class);
                }
                else{
                    intent=new Intent(ResultActivity.this,RecordActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editor.putInt("PageNum",0);
                editor.apply();
                startActivity(intent);
            }
        });
        selectBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4=new Intent(ResultActivity.this, MainActivity.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent4);
            }
        });
    }
}
