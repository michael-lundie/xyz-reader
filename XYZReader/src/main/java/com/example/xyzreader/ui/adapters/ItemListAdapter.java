package com.example.xyzreader.ui.adapters;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.ArticleListActivity;
import com.example.xyzreader.ui.utils.DynamicHeightNetworkImageView;
import com.example.xyzreader.ui.utils.ImageLoaderHelper;
import com.example.xyzreader.ui.utils.PaddingBackgroundColorSpan;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private static final String TAG = ArticleListActivity.class.toString();

    public interface OnItemClickListener {
        //TODO: What item?
        void onItemClick(Uri uri);
    }


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    private Cursor mCursor;
    private final OnItemClickListener mListener;

    public ItemListAdapter(Cursor cursor, OnItemClickListener listener) {
        mCursor = cursor;
        mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ItemListAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Uri uri = ItemsContract.Items.buildItemUri(getItemId(position));
        holder.bind(mListener, uri);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        @BindView(R.id.thumbnail) ImageView thumbnailView;
        @BindView(R.id.article_title) TextView titleView;
        @BindView(R.id.article_subtitle) TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this,view);
        }

        void bind(final OnItemClickListener listener, final Uri uri) {

            SpannableString titleSpan = new SpannableString(mCursor.getString(ArticleLoader.Query.TITLE));
            titleSpan.setSpan(
                    new PaddingBackgroundColorSpan(ContextCompat.getColor(titleView.getContext(), R.color.accent),
                            R.dimen.default_span_padding),
                    0, titleSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleView.setPadding(2,2,2,2);
            titleView.setText(titleSpan);

            Date publishedDate = parsePublishedDate();

            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                subtitleView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            } else {
                subtitleView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                                + "<br/>" + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)));
            }

            Picasso.get()
                    .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                    .into(thumbnailView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Hide our progress bar view on completion of image download.


                        }
                        @Override
                        public void onError(Exception e) {

                        }
                    });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(uri);
                }
            });
        }
    }
}