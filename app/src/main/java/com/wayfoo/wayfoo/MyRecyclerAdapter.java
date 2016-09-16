package com.wayfoo.wayfoo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
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
import java.util.List;

public class MyRecyclerAdapter extends
        RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "Hotel List";
    List<String> favList;
    String k = null;

    public static class CustomViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        protected ImageView imageView;
        protected TextView textView, place, rating, time;
        protected ToggleButton fav;
        CardView card;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            try {
                this.textView = (TextView) view.findViewById(R.id.title1);
                this.rating = (TextView) view.findViewById(R.id.rating);
                this.place = (TextView) view.findViewById(R.id.Place);
                this.time = (TextView) view.findViewById(R.id.time);
                this.fav = (ToggleButton) view.findViewById(R.id.fav);
            } catch (Exception E) {
                E.printStackTrace();
            }
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int i = getAdapterPosition();
            FeedItem feedItem = feedItemList.get(i);
            if (feedItem.getAvail() == 0) {
                Toast.makeText(mc, "Restaurant closed", Toast.LENGTH_SHORT).show();
            } else {
                String pagenext;
                pagenext = Html.fromHtml(feedItem.getTitle()).toString();
                Intent intent = new Intent(mc, Intermediate.class);
                intent.putExtra("title", pagenext);
                intent.putExtra("hotelName", feedItem.getDisName().toString());
                intent.putExtra("table", "1");
                intent.putExtra("tabs", feedItem.getTabs());
                mc.startActivity(intent);
            }
        }
    }

    private static List<FeedItem> feedItemList;

    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList, List<String> fav) {
        MyRecyclerAdapter.feedItemList = feedItemList;
        this.mContext = context;
        favList = fav;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.card_view_row, null, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final FeedItem feedItem = feedItemList.get(i);

        Picasso.with(mContext).load(feedItem.getThumbnail())
                .error(R.mipmap.logo).placeholder(R.mipmap.logo)
                .into(customViewHolder.imageView);

        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/pt-sans.regular.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getDisName()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.textView.setText(SS);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/pt-sans.regular.ttf");
        SS = new SpannableStringBuilder(Html.fromHtml(feedItem.getPlace()));
        SS.setSpan(new CustomTypeFace("", font), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.place.setText(Html.fromHtml(feedItem.getPlace()));
        int rate = Integer.parseInt(feedItem.getRSum());
        int rateNum = Integer.parseInt(feedItem.getRNum());
        float res = 0;
        try {
            res = rate / (float) rateNum;
        } catch (ArithmeticException E) {
        }
        if (Html.fromHtml(String.valueOf(feedItem.getAvail())).equals("0")) {
            customViewHolder.card.setCardBackgroundColor(mc.getResources().getColor(R.color.colorAccent));
        }
        customViewHolder.rating.setText(("" + res).substring(0, 3));

        customViewHolder.time.setText(feedItem.getTime());
        if (favList != null) {
            if (favList.contains(feedItem.getID())) {
                customViewHolder.fav.setChecked(true);
            }
        }
        customViewHolder.fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                k = feedItem.getID();
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
                nameValuePairs.add(new BasicNameValuePair("data", k));
                nameValuePairs.add(new BasicNameValuePair("email", email));

                String jsonResult = "";

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/fav.php");
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
                favList.add(k);
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
                nameValuePairs.add(new BasicNameValuePair("data", k));
                nameValuePairs.add(new BasicNameValuePair("email", email));

                String jsonResult = "";

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://www.wayfoo.com/favrem.php");
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
                Log.d("fav", String.valueOf(favList));
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
