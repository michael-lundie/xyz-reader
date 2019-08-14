package com.example.xyzreader.ui.adapters;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.views.RecycleViewWithSetEmpty;
import com.example.xyzreader.utils.HelperUtils;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for main ItemList view.
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private static final String LOG_TAG = ItemListAdapter.class.getSimpleName();

    public interface OnItemClickListener {
        void onItemClick(Uri uri, int position, int alphaColor, int vibrantColor);
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        // Get current items position from ItemsContract
        Uri uri = ItemsContract.Items.buildItemUri(getItemId(position));
        holder.bind(mListener, uri);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Simple ViewHolder class for our recycler view items.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        @BindView(R.id.thumbnail) ImageView thumbnailView;
        @BindView(R.id.article_title) TextView titleView;

        int alphaColor;
        int baseColor;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this,view);
        }

        /**
         * Class used to bind data to ViewHolder.
         * @param listener reference to the onClickListener interface, handled by owner activity.
         * @param uri contract uri for the article we wish to load
         */
        void bind(final OnItemClickListener listener, final Uri uri) {

            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            final String imageUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);

            Picasso.get().load(imageUrl).into(thumbnailView,
                    PicassoPalette.with(imageUrl, thumbnailView)
                    .use(PicassoPalette.Profile.VIBRANT)
                    .intoCallBack(new PicassoPalette.CallBack() {
                        @Override
                        public void onPaletteLoaded(Palette palette) {
                            Log.i(LOG_TAG, "Palette callback");
                            // Get the returned color from the PicassoPalette library.
                            baseColor = palette.getVibrantColor(
                                    ContextCompat.getColor(mView.getContext(), R.color.primary));

                            alphaColor = HelperUtils.generateSemiOpaque(baseColor, 150);

                            // Set generate color to titleView background.
                            titleView.setBackgroundColor(baseColor);
                        }
                    })
            );

            // Set up our onClickListener interface
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(uri, getAdapterPosition(), alphaColor, baseColor);
                }
            });
        }
    }
}