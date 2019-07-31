package com.example.xyzreader.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.ui.adapters.TextRecyclerAdapter;
import com.example.xyzreader.ui.utils.ImageLoaderHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ArticleDetailFragment.class.toString();

    public static final String ARG_ITEM_ID = "item_id";

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private int mMutedColor = 0xFF333333;

    private View mPhotoContainerView;
    private ImageView mPhotoView;

    private boolean mIsCard = false;

    TextRecyclerAdapter textRecyclerAdapter;

    @BindView(R.id.detail_layout) CoordinatorLayout detailCL;
    @BindView(R.id.body_text_recycler) RecyclerView bodyTextRv;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);

        setHasOptionsMenu(true);
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, mRootView);


//        mScrollView = (ObservableScrollView) mRootView.findViewById(R.id.scrollview);
//        mScrollView.setCallbacks(new ObservableScrollView.Callbacks() {
//            @Override
//            public void onScrollChanged() {
//                mScrollY = mScrollView.getScrollY();
//                getActivityCast().onUpButtonFloorChanged(mItemId, ArticleDetailFragment.this);
//                mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
//                updateStatusBar();
//            }
//        });



//        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
//                        .setType("text/plain")
//                        .setText("Some sample text")
//                        .getIntent(), getString(R.string.action_share)));
//            }
//        });

        bindViews();

        return mRootView;
    }



    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            Log.i(LOG_TAG, "passing today's date");
            return new Date();
        }
    }

    private void bindViews() {
        Log.e(LOG_TAG, "BindView Starting");
        if (mRootView == null) {
            return;
        }
        LinearLayoutManager textRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        bodyTextRv.setLayoutManager(textRecyclerLayoutManager);
        bodyTextRv.setAdapter(textRecyclerAdapter);


        if (mCursor != null) {
            Log.e(LOG_TAG, "Cursor is not null");
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
//            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            Date publishedDate = parsePublishedDate();
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
//                bylineView.setText(Html.fromHtml(
//                        DateUtils.getRelativeTimeSpanString(
//                                publishedDate.getTime(),
//                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                                DateUtils.FORMAT_ABBREV_ALL).toString()
//                                + " by <font color='#ffffff'>"
//                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
//                                + "</font>"));

            } else {
                // If date is before 1902, just show the string
//                bylineView.setText(Html.fromHtml(
//                        outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
//                        + mCursor.getString(ArticleLoader.Query.AUTHOR)
//                                + "</font>"));

            }
            Log.e(LOG_TAG, "Begin Formatting text");
            //TODO: Use recycler view here
            //bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).substring(1500).replaceAll("(\r\n|\n)", "<br />")));
            //bodyView.setText("temp");

//            Log.e(LOG_TAG, "Getting Image");
//            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
//                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
//                        @Override
//                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//
//                            Log.e(LOG_TAG, "Image onResponse method called");
//                            Bitmap bitmap = imageContainer.getBitmap();
//                            if (bitmap != null) {
//                                Log.e(LOG_TAG, "Bitmap not null");
//
//                                Palette p = Palette.generate(bitmap, 12);
//                                mMutedColor = p.getDarkMutedColor(0xFF333333);
//                                mPhotoView.setImageBitmap(imageContainer.getBitmap());
//                                mRootView.findViewById(R.id.meta_bar)
//                                        .setBackgroundColor(mMutedColor);
//                            }
//                        }
//
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            Log.e(LOG_TAG, "Volley error");
//                        }
//                    });
        } else {
            mRootView.setVisibility(View.GONE);
            //TODO: Improve error handling.
//            titleView.setText("N/A");
//            bylineView.setText("N/A" );
//F
        }
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "Creating cursor");
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(LOG_TAG, "Cursor loading finished");
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;

        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(LOG_TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }
        Log.e(LOG_TAG, "Cursor has loaded, setting up adapter");
        String articleString = cursor.getString(ArticleLoader.Query.BODY);

        Log.e(LOG_TAG, "Sending following string to RecyclerAdapter ---> " + articleString);
        textRecyclerAdapter = new TextRecyclerAdapter(articleString);

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

}
