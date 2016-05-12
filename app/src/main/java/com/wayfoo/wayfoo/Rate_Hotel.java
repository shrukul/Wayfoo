package com.wayfoo.wayfoo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrukul on 16/4/16.
 */
public class Rate_Hotel extends Activity implements
        RatingBar.OnRatingBarChangeListener {
    RatingBar getRatingBar;
    TextView countText;
    int count = 0;
    Button rate;
    int curRate;
    ImageView face;
    String hotel = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_hotel);

        findViewsById();
        rate = (Button) findViewById(R.id.rate);
        face = (ImageView) findViewById(R.id.face);
        hotel = getIntent().getExtras().getString("hotel");
        System.out.println("As soon - "+hotel);
        getRatingBar.setOnRatingBarChangeListener(this);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curRate!=0)
                    insertToDatabase();
                else Toast.makeText(getApplicationContext(),"Rate first!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findViewsById() {
        getRatingBar = (RatingBar) findViewById(R.id.getRating);
    }

    public void onRatingChanged(RatingBar rateBar, float rating,
                                boolean fromUser) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        curRate = (int)rating;
        if(curRate<=1 && curRate>=0){
            face.setImageDrawable(getResources().getDrawable(R.drawable.one));
        } else if(curRate<=2 && curRate>1){
            face.setImageDrawable(getResources().getDrawable(R.drawable.two));
        } else if(curRate<=3 && curRate>2){
            face.setImageDrawable(getResources().getDrawable(R.drawable.three));
        } else if(curRate<=4 && curRate>3){
            face.setImageDrawable(getResources().getDrawable(R.drawable.four));
        } else if(curRate<=5 && curRate>4){
            face.setImageDrawable(getResources().getDrawable(R.drawable.five));
        }
        Toast.makeText(Rate_Hotel.this,
                "New Rating: " + curRate, Toast.LENGTH_SHORT).show();
    }

    private void insertToDatabase(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                System.out.println("Hotel "+hotel);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("rate", ""+curRate));
                nameValuePairs.add(new BasicNameValuePair("hotel", hotel));
                String jsonResult = "";

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/rating.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    System.out.println(response);

                    HttpEntity entity = response.getEntity();
                    jsonResult = inputStreamToString(response.getEntity().getContent()).toString();



                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                System.out.println("jsonResult" + jsonResult);
                return "success";
            }

            private StringBuilder inputStreamToString(InputStream is) {
                String rLine = "";
                StringBuilder answer = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                try {
                    while ((rLine = br.readLine()) != null) {
                        answer.append(rLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Answer :" + answer);
                return answer;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                startActivity(new Intent(Rate_Hotel.this,MainActivity.class));
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}