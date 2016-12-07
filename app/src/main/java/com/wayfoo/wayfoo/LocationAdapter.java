package com.wayfoo.wayfoo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.List;

public class LocationAdapter extends
        RecyclerView.Adapter<LocationAdapter.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "locations List";

    public static class CustomViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        protected ImageView imageView;
        protected TextView textView;
        CardView card;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title1);
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int i = getAdapterPosition();
            LocationModel feedItem = feedItemList.get(i);
            PrefManager prefs = new PrefManager(mc);
            prefs.setLocation(feedItem.getTitle().trim());
            Intent newIntent = new Intent(mc, MainActivity.class);
            mc.startActivity(newIntent);
        }
    }

    private static List<LocationModel> feedItemList;

    public LocationAdapter(Context context, List<LocationModel> feedItemList) {
        LocationAdapter.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_row_location, null, true);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        LocationModel feedItem = feedItemList.get(i);

        Picasso.with(mContext).load(feedItem.getThumbnail())
                .error(R.mipmap.logo).placeholder(R.mipmap.logo)
                .into(customViewHolder.imageView);

        Typeface font1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/pt-sans.regular.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getTitle()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.textView.setText(SS);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
