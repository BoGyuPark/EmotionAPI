package com.example.shain.emotionapi;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class Photoview_voice extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView; // variable to hold the image view in our activity_main.xml
    private TextView resultText; // variable to hold the text view in our activity_main.xml
    private static final int RESULT_LOAD_IMAGE  = 100;
    private static final int REQUEST_PERMISSION_CODE = 200;

    SharedPreferences prefs = null;
    SharedPreferences.Editor editor = null;

    int page_num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview_voice);

        //가로모드 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //preference로 페이지 번호 받기
        prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        page_num = prefs.getInt("PageNum",0);

        // initiate our image view and text view
        imageView = (ImageView) findViewById(R.id.imageView);
        resultText = (TextView) findViewById(R.id.resultText);

        Button btn_back = (Button) findViewById(R.id.Back);
        btn_back.setOnClickListener(Photoview_voice.this);

        if(getIntent().hasExtra("byteArray"))
        {
            ImageView previewThumbnail = new ImageView(this);
            Bitmap b = BitmapFactory.decodeByteArray( getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            previewThumbnail.setImageBitmap(b);
            imageView.setImageBitmap(b);
        }

    }

    // when the "GET EMOTION" Button is clicked this function is called
    public void getEmotion(View view) {
        // run the GetEmotionCall class in the background
        Photoview_voice.GetEmotionCall emotionCall = new Photoview_voice.GetEmotionCall(imageView);
        emotionCall.execute();
    }


    @Override
    public void onClick(View vv){
        switch(vv.getId()){
            case (R.id.Back):
                //Toast.makeText(getApplicationContext(), "눌리긴함 ", Toast.LENGTH_LONG).show();
                this.finish();//
                break;

        }
    }

    // This function gets the selected picture from the gallery and shows it on the image view
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        // get the photo URI from the gallery, find the file path from URI and send the file path to ConfirmPhoto
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // a string variable which will store the path to the image in the gallery
            String picturePath= cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
        }
    }


    // convert image to base 64 so that we can send the image to Emotion API
    public byte[] toBase64(ImageView imgPreview) {
        Bitmap bm = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }




    // if permission is not given we get permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(Photoview_voice.this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }




    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    // asynchronous class which makes the API call in the background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class GetEmotionCall extends AsyncTask<Void, Void, String> {


        private final ImageView img;


        GetEmotionCall(ImageView img) {
            this.img = img;
        }


        // this function is called before the API call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultText.setText("Getting results...");
        }


        // this function is called when the API call is made

        @Override
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        protected String doInBackground(Void... params) {
            HttpClient httpclient = HttpClients.createDefault();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            try {
                URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");


                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", "be80873141054e8ab92502be502bd9b1");


                // Request body.The parameter of setEntity converts the image to base64
                request.setEntity(new ByteArrayEntity(toBase64(img)));


                // getting a response and assigning it to the string res
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);


                return res;

            }
            catch (Exception e){
                return "null";
            }

        }


        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(String result) {
            JSONArray jsonArray = null;
            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(result);
                String emotions = "";
                // get the scores object from the results
                for(int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject scores = jsonObject.getJSONObject("scores");
                    double max = 0;
                    String emotion = "";
                    for (int j = 0; j < scores.names().length(); j++) {
                        if (scores.getDouble(scores.names().getString(j)) > max) {
                            max = scores.getDouble(scores.names().getString(j));
                            emotion = scores.names().getString(j);
                        }
                    }
                    emotions += emotion;
                }

                ////////////////////////////////////////////////// emotion 5가지로만 구분 (neutral, sadness, happiness, anger, surprise)
                String s1="contempt";
                String s2="disgust";
                String s3="fear";

                //결과값 intent로 보내기
                Intent myIntent = new Intent(Photoview_voice.this, VoiceActivity.class);

                if(emotions.equals(s1) || emotions.equals(s2) ){
                    resultText.setText("anger");
                    setting_preference("anger");
                    //myIntent.putExtra("resultEmotion","anger");
                    startActivity(myIntent);
                    finish();
                }else if(emotions.equals(s3)){
                    resultText.setText("surprise");
                    setting_preference("surprise");
                    //myIntent.putExtra("resultEmotion","surprise");
                    startActivity(myIntent);
                    finish();
                }else{
                    resultText.setText(emotions);
                    setting_preference(emotions);
                    //myIntent.putExtra("resultEmotion",emotions);
                    startActivity(myIntent);
                    finish();
                }


            } catch (JSONException e) {
                resultText.setText("No emotion detected. 다시시도 Try again later");
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void setting_preference(String emotion){
        if(page_num == 3){
            editor = prefs.edit();
            editor.putString("Emotion_1",emotion);
            editor.apply();
        }
        else if(page_num == 25){
            editor = prefs.edit();
            editor.putString("Emotion_2",emotion);
            editor.apply();
        }
        else{
            editor = prefs.edit();
            editor.putString("Emotion_3",emotion);
            editor.apply();
        }
    }

}

