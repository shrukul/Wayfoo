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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter extends
		RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {

	private final Context mContext;
	private static Context mc;
	static String tag="Hotel List";

	public static class CustomViewHolder extends RecyclerView.ViewHolder
			implements OnClickListener {

		protected ImageView imageView;
		protected TextView textView, place, rating;
		protected ToggleButton fav;
		CardView card;

		public CustomViewHolder(View view) {
			super(view);
			this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
			try {
				this.textView = (TextView) view.findViewById(R.id.title1);
				this.rating = (TextView) view.findViewById(R.id.rating);
				this.place = (TextView) view.findViewById(R.id.Place);
			} catch (Exception E){
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
			if(feedItem.getAvail()==0){
				Toast.makeText(mc,"Restaurant closed",Toast.LENGTH_SHORT).show();
			}else {
				String pagenext;
				pagenext = Html.fromHtml(feedItem.getTitle()).toString();
				Intent intent = new Intent(mc, Intermediate.class);
				intent.putExtra("title", pagenext);
				intent.putExtra("hotelName", feedItem.getDisName().toString());
				intent.putExtra("table", "1");
				mc.startActivity(intent);
			}
		}
	}

	private static List<FeedItem> feedItemList;

	public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
		MyRecyclerAdapter.feedItemList = feedItemList;
		this.mContext = context;
	}

	@Override
	public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.card_view_row, viewGroup,false);

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
		} catch (ArithmeticException E){}
		if(Html.fromHtml(String.valueOf(feedItem.getAvail())).equals("0")){
			customViewHolder.card.setCardBackgroundColor(R.color.colorAccent);
		}
		customViewHolder.rating.setText((""+res).substring(0,3));

	}
	@Override
	public int getItemCount() {
		return (null != feedItemList ? feedItemList.size() : 0);
	}

}
