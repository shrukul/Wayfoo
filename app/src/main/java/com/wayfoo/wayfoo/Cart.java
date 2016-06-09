package com.wayfoo.wayfoo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axle on 12/03/2016.
 */
public class Cart extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView rv;

    private static final String TAG = "RecyclerViewExample";
    private List<FeedItemHotel> persons,mm;
    private DatabaseHandler db;
    private Cursor c;
    private CartAdapter adapter;
    Button checkout;
    private PrefManager pref;
    private float amt=0;
    String json,phone;
    String hotel,table,addr;
    TextView amount;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_card_view);

        PrefManager prefs = new PrefManager(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        hotel=prefs.getTitle();
        addr = getIntent().getExtras().getString("addr");
        amount = (TextView) findViewById(R.id.amount);
        amount.setText("₹ "+prefs.getPriceSum());
        System.out.println("Addr is "+addr);
        table=prefs.getTable();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        checkout = (Button) findViewById(R.id.checkout);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cart");

        pref = new PrefManager(this);
        phone = pref.getMobileNumber();
        rv = (RecyclerView)findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(this));

        db=new DatabaseHandler(this);
        initializeData();
        adapter = new CartAdapter(this,persons);
        rv.setAdapter(adapter);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<FeedItemHotel> contacts = db.getAllContacts();

                for (FeedItemHotel cn : contacts) {
                    FeedItemHotel item=new FeedItemHotel();
                    if(cn.getAmt().toString().equals("0"))
                    {}else {
                        item.setTitle(cn.getTitle());
                        item.setType(cn.getType());
                        item.setPrice(cn.getPrice());
                        item.setVeg(cn.getVeg());
                        item.setAmt(cn.getAmt());
                        persons.add(item);
                        //amt += (Float.parseFloat(cn.getAmt())*Float.parseFloat(cn.getPrice()));
                    }
                }
                int k = 1;
                mm = new ArrayList<FeedItemHotel>();

                for(k = 0 ; k < persons.size() ; k++){
                    if(k<persons.size()/2){
                        mm.add(persons.get(k));
                        amt += (Float.parseFloat(persons.get(k).getAmt())*Float.parseFloat(persons.get(k).getPrice()));
                    }
                }
                String text = String.format("%.2f", amt);
                Toast.makeText(Cart.this,"Total " + text, Toast.LENGTH_LONG).show();
                Gson gson = new Gson();
                Type type = new TypeToken<List<FeedItemHotel>>() {}.getType();
                json = gson.toJson(mm, type);
                Log.d("Json",json);
                if(amt > 0)
                    insertToDatabase();
                else
                    Toast.makeText(getApplicationContext(),"Select at least one item",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Cart.this,AdditionalInfo.class));
        finish();
    }

    private void initializeData() {

        persons = new ArrayList<FeedItemHotel>();

        FeedItemHotel feed=new FeedItemHotel();
        List<FeedItemHotel> contacts = db.getAllContacts();

        for (FeedItemHotel cn : contacts) {
            FeedItemHotel item=new FeedItemHotel();
            if(cn.getAmt().toString().equals("0"))
            {}else {
                item.setTitle(cn.getTitle());
                item.setType(cn.getType());
                item.setPrice(cn.getPrice());
                item.setVeg(cn.getVeg());
                item.setAmt(cn.getAmt());
                persons.add(item);
            }
        }
    }

    private void insertToDatabase(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            final ProgressDialog progressDialog = new ProgressDialog(Cart.this,
                    R.style.AppTheme_Dark_Dialog);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Placing your order...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                PrefManager pref = new PrefManager(getApplicationContext());
                String email=pref.getEmail();
                String regid=pref.getRegId();
                String dispName=pref.getHotelName();
                nameValuePairs.add(new BasicNameValuePair("data", json));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("amt", String.valueOf(amt)));
                nameValuePairs.add(new BasicNameValuePair("hotel", hotel));
                nameValuePairs.add(new BasicNameValuePair("hotelName", dispName));
                nameValuePairs.add(new BasicNameValuePair("table", table));
                nameValuePairs.add(new BasicNameValuePair("phone", phone));
                nameValuePairs.add(new BasicNameValuePair("regid", regid));
                nameValuePairs.add(new BasicNameValuePair("addr", addr));

                String jsonResult = "";

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/orderList.php");
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
                progressDialog.dismiss();
                startActivity(new Intent(Cart.this,MainActivity.class));
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
