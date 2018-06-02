package com.example.shain.emotionapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CROP = 3;

    Button btn_takePictrue;
    String mCurrentPhotoPath;
    Uri photoURI = null;
    Boolean album = false;

    ArrayList<String> arr = new ArrayList<>();
    ArrayList<Integer> arr_image = new ArrayList<>();
    ArrayList<Integer> arr_mplay = new ArrayList<>();

    SharedPreferences prefs = null;
    SharedPreferences.Editor editor = null;

    Button prevbtn = null;
    Button nextbtn = null;
    Button exitbtn = null;
    Button logbtn=null;

    Button camera_btn;
    Button result_btn;

    int page_num=0;
    int point=0; //점수
    Boolean layout_btn_visible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //화면 꺼지지 않게

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        editor=prefs.edit();

        page_num = prefs.getInt("PageNum",0);

        BroadcastReceiver screenOnOff = new BroadcastReceiver()
        {
            public static final String ScreenOff = "android.intent.action.SCREEN_OFF";
            public static final String ScreenOn = "android.intent.action.SCREEN_ON";

            public void onReceive(Context contex, Intent intent)
            {
                if (intent.getAction().equals(ScreenOff))
                {
                    editor = prefs.edit();
                    editor.putInt("PageNum",page_num);
                    editor.apply();
                }
                else if (intent.getAction().equals(ScreenOn))
                {
                    //page_num = prefs.getInt("PageNum",0);
                }
            }
        };
        registerReceiver(screenOnOff, intentFilter);

        prevbtn = (Button) findViewById(R.id.read_prev_btn);
        nextbtn = (Button) findViewById(R.id.read_next_btn);
        exitbtn = (Button) findViewById(R.id.read_exit_btn);
        camera_btn = (Button) findViewById(R.id.read_camera_btn);
        logbtn=(Button)findViewById(R.id.log_btn);

        result_btn = (Button)findViewById(R.id.emotion_reuslt_btn);

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"카메라", Toast.LENGTH_LONG).show();
                editor = prefs.edit();
                editor.putInt("PageNum",page_num);
                editor.apply();
                dispatchTakePicutreIntent();
            }
        });

        final LinearLayout readlayout = (LinearLayout) findViewById(R.id.read_layout);

        final TextView text = (TextView) findViewById(R.id.read_text);

        init_story();

        text.setText(arr.get(page_num));
        readlayout.setBackgroundResource(arr_image.get(page_num));
        if(page_num == 0) {
            prevbtn.setVisibility(View.INVISIBLE);
            nextbtn.setVisibility(View.VISIBLE);
            camera_btn_visible();
        }
        else if(page_num == arr.size()-1){
            prevbtn.setVisibility(View.VISIBLE);
            nextbtn.setVisibility(View.INVISIBLE);
            camera_btn_visible();
        }
        else{
            prevbtn.setVisibility(View.VISIBLE);
            nextbtn.setVisibility(View.VISIBLE);
            camera_btn_visible();
        }

        readlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout_btn_visible) {
                    exitbtn.setVisibility(View.INVISIBLE);
                    prevbtn.setVisibility(View.INVISIBLE);
                    nextbtn.setVisibility(View.INVISIBLE);
                    layout_btn_visible = false;
                }
                else{
                    exitbtn.setVisibility(View.VISIBLE);
                    if(page_num == 0){
                        prevbtn.setVisibility(View.INVISIBLE);
                        nextbtn.setVisibility(View.VISIBLE);
                    }
                    else{
                        prevbtn.setVisibility(View.VISIBLE);
                        nextbtn.setVisibility(View.VISIBLE);
                    }
                    layout_btn_visible = true;
                }
            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_num = page_num - 1;
                if (page_num == 0) {
                    prevbtn.setVisibility(View.INVISIBLE);
                    nextbtn.setVisibility(View.VISIBLE);
                }
                else {
                    prevbtn.setVisibility(View.VISIBLE);
                    nextbtn.setVisibility(View.VISIBLE);
                }
                text.setText(arr.get(page_num));
                readlayout.setBackgroundResource(arr_image.get(page_num));
                camera_btn_visible();
            }

        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_num = page_num + 1;
                if(page_num==arr.size()){
                    editor.putInt("point",point);
                    editor.apply();
                    Intent intent=new Intent(ReadActivity.this,ResultActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }else{
                    prevbtn.setVisibility(View.VISIBLE);
                    nextbtn.setVisibility(View.VISIBLE);

                    text.setText(arr.get(page_num));
                    readlayout.setBackgroundResource(arr_image.get(page_num));
                }
                camera_btn_visible();
            }
        });

        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("Layout",0);
                editor.apply();
                Intent intent=new Intent(ReadActivity.this,LogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subIntent = getIntent();
                String result = subIntent.getStringExtra("resultEmotion");
                result_btn.setText(result);

            }
        });
    }

    private void dispatchTakePicutreIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile(); //사진찍은 후 저장할 임시 파일

            }catch(IOException ex){
                Toast.makeText(getApplicationContext(),"createImageFile Failed", Toast.LENGTH_LONG).show();
            }

            if(photoFile!=null){
                photoURI = Uri.fromFile(photoFile);     //임시 파일의 위치, 경로 가져옴
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);      //임시 파일 위치에 저장
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException{
        //특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
        String imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis())+".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
        mCurrentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }

    private void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX",200); // crop한 이미지의 x축 크기
        //cropIntent.putExtra("outputY",200); // crop한 이미지의 y축 크기
        //cropIntent.putExtra("aspectX",1); // crop한 박스의 x축 비율
        //cropIntent.putExtra("aspectY",1); // crop한 박스의 y축 비율
        cropIntent.putExtra("scale", true);

        if(album==false){
            cropIntent.putExtra("output",photoURI); //크랍된 이미지를 해당 경로에 저장

        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "onActivityResult : RESULT_NOT_OK", Toast.LENGTH_LONG).show();
        }else{
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE:
                    cropImage();
                    break;
                case REQUEST_IMAGE_CROP:

                    Bitmap photo = BitmapFactory.decodeFile(photoURI.getPath());

                    //Photoview 액티비티에 Bitmap intent 전달한다
                    Intent i = new Intent(this, photoview.class);
                    Bitmap b = photo; // your bitmap
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 75, bs);
                    i.putExtra("byteArray", bs.toByteArray());
                    startActivity(i);

                    //imageview에 뿌리기
                    //iv_capture.setImageBitmap(photo);



                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); //동기화
                    if(album == false){
                        mediaScanIntent.setData(photoURI);      //동기화
                    }
                    this.sendBroadcast(mediaScanIntent);        //동기화
                    finish();
                    break;
            }
        }

    }

    public void camera_btn_visible(){
        if(camera_page(page_num)){
            camera_btn.setVisibility(View.VISIBLE);
        }
        else{
            camera_btn.setVisibility(View.INVISIBLE);
        }
    }

    public Boolean camera_page(int page_num){
        // 게임 첫 페이지만 사진 찍을 수 있도록 수정.
        if(page_num == 3)
            return true;
        else if(page_num == 25)
            return true;
        else if(page_num == 36)
            return true;
        else
            return false;
    }

    public void init_story(){

        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        String[] tx=getResources().getStringArray(R.array.script);
        String result_1 = prefs.getString("Emotion_1", "default");
        String result_2 = prefs.getString("Emotion_2", "default");
        String result_3 = prefs.getString("Emotion_3", "default");

        insert_page(0,R.drawable.story_1,tx[0].toString(),R.raw.script_0);
        insert_page(1,R.drawable.story_1,tx[1].toString(),R.raw.script_1);
        insert_page(2,R.drawable.story_1,tx[2].toString(),R.raw.script_2);

        if(result_1.equals("anger")) {
            insert_page(3, R.drawable.story_2_anger, tx[3].toString(), R.raw.script_3);
            insert_page(4, R.drawable.story_2_anger, tx[4].toString(), R.raw.script_4);
            insert_page(5, R.drawable.story_2_anger, tx[5].toString(), R.raw.script_5);
        }
        else if(result_1.equals("sadness")){
            insert_page(3, R.drawable.story_2_sad, tx[3].toString(), R.raw.script_3);
            insert_page(4, R.drawable.story_2_sad, tx[4].toString(), R.raw.script_4);
            insert_page(5, R.drawable.story_2_sad, tx[5].toString(), R.raw.script_5);
            point++;
        }
        else if(result_1.equals("happiness")){
            insert_page(3, R.drawable.story_2_happy, tx[3].toString(), R.raw.script_3);
            insert_page(4, R.drawable.story_2_happy, tx[4].toString(), R.raw.script_4);
            insert_page(5, R.drawable.story_2_happy, tx[5].toString(), R.raw.script_5);
        }
        else if(result_1.equals("surprise")){
            insert_page(3, R.drawable.story_2_surprise, tx[3].toString(), R.raw.script_3);
            insert_page(4, R.drawable.story_2_surprise, tx[4].toString(), R.raw.script_4);
            insert_page(5, R.drawable.story_2_surprise, tx[5].toString(), R.raw.script_5);
        }
        else if(result_1.equals("neutral")){
            insert_page(3, R.drawable.story_2_neutral, tx[3].toString(), R.raw.script_3);
            insert_page(4, R.drawable.story_2_neutral, tx[4].toString(), R.raw.script_4);
            insert_page(5, R.drawable.story_2_neutral, tx[5].toString(), R.raw.script_5);
        }
        else{
            insert_page(3, R.drawable.story_2, tx[3].toString(), R.raw.script_3);
            insert_page(4, R.drawable.story_2, tx[4].toString(), R.raw.script_4);
            insert_page(5, R.drawable.story_2, tx[5].toString(), R.raw.script_5);
        }

        insert_page(6,R.drawable.story_3,tx[6].toString(),R.raw.script_6);
        insert_page(7,R.drawable.story_3,tx[7].toString(),R.raw.script_7);
        insert_page(8,R.drawable.story_3,tx[8].toString(),R.raw.script_8);
        insert_page(9,R.drawable.story_3,tx[9].toString(),R.raw.script_9);
        insert_page(10,R.drawable.story_4,tx[10].toString(),R.raw.script_10);
        insert_page(11,R.drawable.story_4,tx[11].toString(),R.raw.script_11);
        insert_page(12,R.drawable.story_4,tx[12].toString(),R.raw.script_12);
        insert_page(13,R.drawable.story_4,tx[13].toString(),R.raw.script_13);
        insert_page(14,R.drawable.story_4,tx[14].toString(),R.raw.script_14);
        insert_page(15,R.drawable.story_5,tx[15].toString(),R.raw.script_15);
        insert_page(16,R.drawable.story_5,tx[16].toString(),R.raw.script_16);
        insert_page(17,R.drawable.story_5,tx[17].toString(),R.raw.script_17);
        insert_page(18,R.drawable.story_5,tx[18].toString(),R.raw.script_18);
        insert_page(19,R.drawable.story_6,tx[19].toString(),R.raw.script_19);
        insert_page(20,R.drawable.story_6,tx[20].toString(),R.raw.script_20);
        insert_page(21,R.drawable.story_6,tx[21].toString(),R.raw.script_21);
        insert_page(22,R.drawable.story_6,tx[22].toString(),R.raw.script_22);
        insert_page(23,R.drawable.story_7,tx[23].toString(),R.raw.script_23);
        insert_page(24,R.drawable.story_7,tx[24].toString(),R.raw.script_24);

        if(result_2.equals("anger")) {
            insert_page(25,R.drawable.story_8_anger,tx[25].toString(),R.raw.script_25);
            insert_page(26,R.drawable.story_8_anger,tx[26].toString(),R.raw.script_26);
        }
        else if(result_2.equals("sadness")){
            insert_page(25,R.drawable.story_8_sad,tx[25].toString(),R.raw.script_25);
            insert_page(26,R.drawable.story_8_sad,tx[26].toString(),R.raw.script_26);
        }
        else if(result_2.equals("happiness")){
            insert_page(25,R.drawable.story_8_happy,tx[25].toString(),R.raw.script_25);
            insert_page(26,R.drawable.story_8_happy,tx[26].toString(),R.raw.script_26);
            point++;
        }
        else if(result_2.equals("surprise")){
            insert_page(25,R.drawable.story_8_surprise,tx[25].toString(),R.raw.script_25);
            insert_page(26,R.drawable.story_8_surprise,tx[26].toString(),R.raw.script_26);
        }
        else if(result_2.equals("neutral")){
            insert_page(25,R.drawable.story_8_neutral,tx[25].toString(),R.raw.script_25);
            insert_page(26,R.drawable.story_8_neutral,tx[26].toString(),R.raw.script_26);
        }
        else{
            insert_page(25,R.drawable.story_8,tx[25].toString(),R.raw.script_25);
            insert_page(26,R.drawable.story_8,tx[26].toString(),R.raw.script_26);
        }

        insert_page(27,R.drawable.story_9,tx[27].toString(),R.raw.script_27);
        insert_page(28,R.drawable.story_9,tx[28].toString(),R.raw.script_28);
        insert_page(29,R.drawable.story_9,tx[29].toString(),R.raw.script_29);
        insert_page(30,R.drawable.story_10,tx[30].toString(),R.raw.script_30);
        insert_page(31,R.drawable.story_10,tx[31].toString(),R.raw.script_31);
        insert_page(32,R.drawable.story_10,tx[32].toString(),R.raw.script_32);
        insert_page(33,R.drawable.story_11,tx[33].toString(),R.raw.script_33);
        insert_page(34,R.drawable.story_11,tx[34].toString(),R.raw.script_34);
        insert_page(35,R.drawable.story_11,tx[35].toString(),R.raw.script_35);

        if(result_3.equals("anger")) {
            insert_page(36,R.drawable.story_12_anger,tx[36].toString(),R.raw.script_36);
            insert_page(37,R.drawable.story_12_anger,tx[37].toString(),R.raw.script_37);
            point++;
        }
        else if(result_3.equals("sadness")){
            insert_page(36,R.drawable.story_12_sad,tx[36].toString(),R.raw.script_36);
            insert_page(37,R.drawable.story_12_sad,tx[37].toString(),R.raw.script_37);
        }
        else if(result_3.equals("happiness")){
            insert_page(36,R.drawable.story_12_happy,tx[36].toString(),R.raw.script_36);
            insert_page(37,R.drawable.story_12_happy,tx[37].toString(),R.raw.script_37);
        }
        else if(result_3.equals("surprise")){
            insert_page(36,R.drawable.story_12_surprise,tx[36].toString(),R.raw.script_36);
            insert_page(37,R.drawable.story_12_surprise,tx[37].toString(),R.raw.script_37);
        }
        else if(result_3.equals("neutral")){
            insert_page(36,R.drawable.story_12_neutral,tx[36].toString(),R.raw.script_36);
            insert_page(37,R.drawable.story_12_neutral,tx[37].toString(),R.raw.script_37);
        }
        else{
            insert_page(36,R.drawable.story_12,tx[36].toString(),R.raw.script_36);
            insert_page(37,R.drawable.story_12,tx[37].toString(),R.raw.script_37);
        }

        insert_page(38,R.drawable.story_13,tx[38].toString(),R.raw.script_38);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*
        private void ShowDialog()
        {
            LayoutInflater dialog = LayoutInflater.from(this);
            final View dialogLayout = dialog.inflate(R.layout.dialog, null);
            final Dialog myDialog = new Dialog(this);

            myDialog.setContentView(dialogLayout);
            myDialog.show();

            Button btn_play = (Button)dialogLayout.findViewById(R.id.btn_dialog_play);

            btn_play.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    myDialog.cancel();
                }
            });
        }
    */
    private void insert_page(int index, int drawble, String script, int mplay){
        arr_image.add(index,drawble);
        arr.add(index, script);
        arr_mplay.add(index, mplay);
    }
}


