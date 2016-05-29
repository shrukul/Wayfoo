package com.wayfoo.wayfoo.instamojo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wayfoo.wayfoo.MainActivity;
import com.wayfoo.wayfoo.R;
import com.wayfoo.wayfoo.helper.PrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi0184 on 29/05/16.
 */
public class Payment extends AppCompatActivity {

    EditText rs;
    Button rssubmit;
    BufferedReader reader;
    String text = "";
    String link = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        rs = (EditText) findViewById(R.id.rs);
        rssubmit = (Button) findViewById(R.id.rssubmit);

        rssubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Here");
                submitRequest(rs.getText().toString());
            }
        });
    }

    private void submitRequest(String s) {
        AsyncReq sendPostReqAsyncTask = new AsyncReq();
        sendPostReqAsyncTask.execute(s);
    }

    public class AsyncReq extends AsyncTask<String, Void, String> {

        final ProgressDialog progressDialog = new ProgressDialog(Payment.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Making Request...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("purpose", "Testing"));
            nameValuePairs.add(new BasicNameValuePair("amount", params[0]));
            nameValuePairs.add(new BasicNameValuePair("buyer_name","Shrukul"));

            String jsonResult = "";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(
                        "https://www.instamojo.com/api/1.1/payment-requests/");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpPost.setHeader("X-Api-Key", "12f311b75258c0f5a7e63970a1d4aa86");
                httpPost.setHeader("X-Auth-Token", "49cb5d9f5625d4c9d53f7c04f11224f8");

                HttpResponse response = httpClient.execute(httpPost);

                System.out.println(response);

                HttpEntity entity = response.getEntity();
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();



            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            System.out.println("jsonResult" + jsonResult);
            try {
                JSONObject jsonObj = new JSONObject(jsonResult);
                JSONObject payment_request = jsonObj.getJSONObject("payment_request");
                link = payment_request.getString("longurl");
                System.out.println(jsonObj);
                System.out.println(payment_request);
                System.out.println(link);

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            progressDialog.dismiss();
            Intent it = new Intent(Payment.this, Web.class);
            System.out.println("url : "+link);
            it.putExtra("longurl",link);
            startActivity(it);
        }
    }
}