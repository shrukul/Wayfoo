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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.wayfoo.wayfoo.helper.PrefManager;

import java.util.List;

public class CartAdapter extends
        RecyclerView.Adapter<CartAdapter.CustomViewHolder> {

    private final Context mContext;
    private static Context mc;
    static String tag = "Menu";
    static MyApplication app;
    String hotel;

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView textView, price, amt;
        Button plus;
        CardView card;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.veg);
            this.textView = (TextView) view.findViewById(R.id.title1);
            this.price = (TextView) view.findViewById(R.id.price);
            this.plus = (Button) view.findViewById(R.id.add);
            this.amt = (TextView) view.findViewById(R.id.amt);
            mc = view.getContext();
            card = (CardView) view.findViewById(R.id.YogaCard);
        }

    }

    private static List<FeedItemHotel> feedItemList;
    private static TextView amo;

    public CartAdapter(Context context, List<FeedItemHotel> feedItemList, TextView amount,MyApplication app,String Hotel) {
        CartAdapter.feedItemList = feedItemList;
        CartAdapter.amo = amount;
        this.mContext = context;
        this.app = app;
        this.hotel = Hotel;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_view, null, false);
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT));
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
        SS = new SpannableStringBuilder(
                Html.fromHtml(feedItem.getPrice()));
        SS.setSpan(new CustomTypeFace("", font1), 0, SS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        customViewHolder.price.setText(SS);
        Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.veg);
        Bitmap b2 = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.nonveg);
        if (feedItem.getVeg().toString().equals("0")) {
            customViewHolder.imageView.setImageBitmap(b);
        } else {
            customViewHolder.imageView.setImageBitmap(b2);
        }
        customViewHolder.plus.setBackgroundResource(R.drawable.cross);
        customViewHolder.amt.setText(feedItem.getAmt());

        customViewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(customViewHolder.amt.getText().toString());
                c--;
                DatabaseHandler db = new DatabaseHandler(mContext);
                List<FeedItemHotel> contacts = db.getAllContacts();
                int x = 0;
                for (FeedItemHotel cn : contacts) {
                    if (cn.getTitle().equals(customViewHolder.textView.getText().toString().trim())) {
                        x = cn.getID();
                    }
                }
                if (c == 0) {
                    feedItemList.remove(i);
                    db.updateContact(new FeedItemHotel(x, feedItem.getTitle(), feedItem.getPrice(), feedItem.getVeg(),
                            String.valueOf(c), feedItem.getType(), feedItem.getItemID()));
                    Log.d("Delete", String.valueOf(i) + " " + x);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, feedItemList.size());
                } else {
                    customViewHolder.amt.setText(String.valueOf(c));
                    db.updateContact(new FeedItemHotel(x, feedItem.getTitle(), feedItem.getPrice(), feedItem.getVeg(),
                            String.valueOf(c), feedItem.getType(), feedItem.getItemID()));
                    Log.d("Changed", String.valueOf(i) + "  " + x);

                }

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
                feedItem.setAmt("" + c);
//                notifyDataSetChanged();
                db.close();
                PrefManager prefs = new PrefManager(mContext);
                int temp = prefs.getPriceSum();
                int sum = temp - Integer.parseInt(customViewHolder.price.getText().toString());
                prefs.setPriceSum(sum);
                amo.setText("₹ " + prefs.getPriceSum());
/*                Intent it = new Intent(mc,Cart.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mc.startActivity(it);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}
