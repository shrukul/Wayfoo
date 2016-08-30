package com.wayfoo.wayfoo;

/**
 * Created by Axle on 09/02/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.List;

public class MyRecyclerAdapterHotel extends
        RecyclerView.Adapter<MyRecyclerAdapterHotel.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "Menu";

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView textView, price, amt;
        Button plus, edit, minus;
        CardView card;
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
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
        }

    }

    private List<FeedItemHotel> feedItemList;

    public MyRecyclerAdapterHotel(Context context, List<FeedItemHotel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
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
                if (x != -1)
                    db.updateContact(new FeedItemHotel(x, feedItem.getTitle(), feedItem.getPrice(), feedItem.getVeg(),
                            String.valueOf(c), feedItem.getType(), feedItem.getItemID()));
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
                    if (x != -1)
                        db.updateContact(new FeedItemHotel(x, feedItem.getTitle(), feedItem.getPrice(), feedItem.getVeg(),
                                String.valueOf(c), feedItem.getType(), feedItem.getItemID()));
                    db.close();
                    PrefManager prefs = new PrefManager(mContext);
                    int temp = prefs.getPriceSum();
                    notifyDataSetChanged();
                    int sum = temp - Integer.parseInt(customViewHolder.price.getText().toString().substring(1));
                    prefs.setPriceSum(sum);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
