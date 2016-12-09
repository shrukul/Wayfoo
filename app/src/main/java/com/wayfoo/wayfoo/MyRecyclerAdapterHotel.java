package com.wayfoo.wayfoo;

/**
 * Created by Axle on 09/02/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.wayfoo.wayfoo.helper.MCrypt;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MyRecyclerAdapterHotel extends
        RecyclerView.Adapter<MyRecyclerAdapterHotel.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "Menu";
    static MyApplication app;
    String hotel;
    String k = null;
    List<String> favList;
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView textView, price, amt;
        Button plus, edit, minus;
        CardView card;
        protected ToggleButton fav;
        LinearLayout ll;
        EditText note;
        TextView note_text;


        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.veg);
            this.textView = (TextView) view.findViewById(R.id.title1);
            this.price = (TextView) view.findViewById(R.id.price);
            this.plus = (Button) view.findViewById(R.id.add);
            this.minus = (Button) view.findViewById(R.id.minus);
            this.amt = (TextView) view.findViewById(R.id.amt);
            this.fav = (ToggleButton) view.findViewById(R.id.favB);
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
        }

    }

    private List<FeedItemHotel> feedItemList;

    public MyRecyclerAdapterHotel(Context context, List<FeedItemHotel> feedItemList,MyApplication app,String Hotel,List<String> fav) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.app = app;
        this.hotel = Hotel;
        favList = fav;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.card_view_row_hotel, null, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        final FeedItemHotel feedItem = feedItemList.get(i);
        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/pt-sans.regular.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getTitle()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.textView.setText(SS);
        //customViewHolder.textView.setText(feedItem.getTitle());
        SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getPrice()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        String finalPrice = "₹" + SS;
        customViewHolder.price.setText(finalPrice);
        Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.veg);
        Bitmap b2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.non_veg);
        if (feedItem.getVeg().toString().equals("0")) {
            customViewHolder.imageView.setImageBitmap(b);
        } else {
            customViewHolder.imageView.setImageBitmap(b2);
        }
        if (favList != null) {
            DatabaseHandler db = new DatabaseHandler(mContext);
            List<FeedItemHotel> contacts = db.getAllContacts();
            int y = -1;
            for (FeedItemHotel cn : contacts) {
                if (cn.getTitle().trim().equals(customViewHolder.textView.getText().toString().trim())) {
                    y = cn.getID();
                }
            }
            if (y!=-1 && favList.contains(String.valueOf(y))) {
                customViewHolder.fav.setChecked(true);
            }
        }
        customViewHolder.amt.setText(feedItem.getAmt());
        customViewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(customViewHolder.amt.getText().toString());
                c++;
                customViewHolder.amt.setText(String.valueOf(c));
                feedItem.setAmt("" + c);
                DatabaseHandler db = new DatabaseHandler(mContext);
                int x = -1;
                List<FeedItemHotel> contacts = db.getAllContacts();
                for (FeedItemHotel cn : contacts) {
                    if (cn.getTitle().trim().equals(customViewHolder.textView.getText().toString().trim())) {
                        x = cn.getID();
                    }
                }
                if (x != -1) {
                    db.updateContact(new FeedItemHotel(x, feedItem.getTitle(), feedItem.getPrice(), feedItem.getVeg(),
                            String.valueOf(c), feedItem.getType(), feedItem.getItemID()));
                    Product product = new Product()
                            .setName(hotel)
                            .setPrice(Double.parseDouble(feedItem.getPrice()))
                            .setVariant(feedItem.getTitle())
                            .setId(String.valueOf(hotel + "-" + feedItem.getID()))
                            .setQuantity(Integer.parseInt(feedItem.getAmt()));

                    ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD);
                    Tracker tracker = ((MyApplication) app).getTracker();
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Shopping steps")
                            .setAction("Add to cart")
                            .setLabel(hotel + "-" + feedItem.getTitle())
                            .addProduct(product)
                            .setProductAction(productAction)
                            .build());
                }
                db.close();
                notifyDataSetChanged();
                PrefManager prefs = new PrefManager(mContext);
                int temp = prefs.getPriceSum();
                int sum = temp + Integer.parseInt(customViewHolder.price.getText().toString().substring(1));
                prefs.setPriceSum(sum);
            }
        });

        customViewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(customViewHolder.amt.getText().toString());
                if (c > 0) {
                    c--;
                    customViewHolder.amt.setText(String.valueOf(c));
                    feedItem.setAmt("" + c);
                    DatabaseHandler db = new DatabaseHandler(mContext);
                    int x = -1;
                    List<FeedItemHotel> contacts = db.getAllContacts();
                    for (FeedItemHotel cn : contacts) {
                        if (cn.getTitle().trim().equals(customViewHolder.textView.getText().toString().trim())) {
                            x = cn.getID();
                        }
                    }
                    if (x != -1) {
                        db.updateContact(new FeedItemHotel(x, feedItem.getTitle(), feedItem.getPrice(), feedItem.getVeg(),
                                String.valueOf(c), feedItem.getType(), feedItem.getItemID()));
                        Product product = new Product()
                                .setName(hotel)
                                .setPrice(Double.parseDouble(feedItem.getPrice()))
                                .setVariant(feedItem.getTitle())
                                .setId(String.valueOf(hotel + "-" + feedItem.getID()))
                                .setQuantity(Integer.parseInt(feedItem.getAmt()));

                        ProductAction productAction = new ProductAction(ProductAction.ACTION_REMOVE);
                        Tracker tracker = ((MyApplication) app).getTracker();
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Shopping steps")
                                .setAction("Remove from cart")
                                .setLabel(hotel + "-" + feedItem.getTitle())
                                .addProduct(product)
                                .setProductAction(productAction)
                                .build());
                    }
                    db.close();
                    PrefManager prefs = new PrefManager(mContext);
                    int temp = prefs.getPriceSum();
                    notifyDataSetChanged();
                    int sum = temp - Integer.parseInt(customViewHolder.price.getText().toString().substring(1));
                    prefs.setPriceSum(sum);
                }
            }
        });
        customViewHolder.fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseHandler db = new DatabaseHandler(mContext);
                List<FeedItemHotel> contacts = db.getAllContacts();
                for (FeedItemHotel cn : contacts) {
                    if (cn.getTitle().trim().equals(customViewHolder.textView.getText().toString().trim())) {
                        k = String.valueOf(cn.getID());
                    }
                }
                System.out.println(k);
                if (isChecked)
                    insertToDatabase();
                else
                    insertToDatabase2();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    private void insertToDatabase() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            final ProgressDialog progressDialog = new ProgressDialog(mc,
                    R.style.MyMaterialTheme_AlertDialogTheme);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Adding to favourites...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                PrefManager pref = new PrefManager(mc);
                String email = pref.getEmail();
                MCrypt mcrypt = new MCrypt();
                try {
                    nameValuePairs.add(new BasicNameValuePair("data", MCrypt.bytesToHex(mcrypt.encrypt(k))));
                    nameValuePairs.add(new BasicNameValuePair("email", MCrypt.bytesToHex(mcrypt.encrypt(email))));
                    nameValuePairs.add(new BasicNameValuePair("hotel", MCrypt.bytesToHex(mcrypt.encrypt(pref.getTitle()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String jsonResult = "";

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/php/favHotel.php");
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

                try {
                    favList.add(k);
                    PrefManager pref = new PrefManager(mc);
                    String fav = pref.getFavMenu();
                    if(fav==null || fav.equals("")){
                        fav = k;
                    } else {
                        fav = fav + "," + k;
                    }
                    pref.setFavMenu(fav);
                }catch(NullPointerException e){

                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


    private void insertToDatabase2() {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {


            final ProgressDialog progressDialog = new ProgressDialog(mc,
                    R.style.MyMaterialTheme_AlertDialogTheme);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("removing from favourites...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                PrefManager pref = new PrefManager(mc);
                String email = pref.getEmail();
                MCrypt mcrypt = new MCrypt();
                try {
                    nameValuePairs.add(new BasicNameValuePair("data", MCrypt.bytesToHex(mcrypt.encrypt(k))));
                    nameValuePairs.add(new BasicNameValuePair("email", MCrypt.bytesToHex(mcrypt.encrypt(email))));
                    nameValuePairs.add(new BasicNameValuePair("hotel", MCrypt.bytesToHex(mcrypt.encrypt(pref.getTitle()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String jsonResult = "";

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/php/favremHotel.php");
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
                int i = 0;
                for (String x : favList) {
                    if (x.equals(k)) {
                        break;
                    }
                    i++;
                }
                favList.remove(i);
                PrefManager pref = new PrefManager(mc);
                String fav = pref.getFavMenu();
                List<String> fList = new LinkedList(Arrays.asList(fav.split(",")));
                if(fList.contains(k)){
                    fList.remove(k);
                }
                String snew = android.text.TextUtils.join(",", fList);
                pref.setFavMenu(snew);
                Log.e("ff", "ff " + snew);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
