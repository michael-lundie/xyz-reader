package com.example.xyzreader.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.adapters.ItemPagerAdapter;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ArticleDetailActivity.class.toString();

    @BindView(R.id.view_pager) ViewPager mPager;
    @BindView(R.id.hero_image)ImageView heroIv;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout ctLayout;
    @BindView(R.id.toolbar)Toolbar toolbar;

    private Cursor mCursor;
    private long mStartId;
    private long selectedItemId;

    private ItemPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new ItemPagerAdapter(getSupportFragmentManager(), mCursor);

        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(LOG_TAG, "Selecting the current position --> " + position);
                setToolbarTitleAndImage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                super.onPageScrollStateChanged(state);
//                mUpButton.animate()
//                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
//                        .setDuration(300);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (mCursor != null) {
//                    mCursor.moveToPosition(position);
//                }
//                mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
//                updateUpButtonPosition();
//            }
//        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                Log.e(LOG_TAG, "Retrieving URI <<-- " + getIntent().getData());

                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                //TODO: Pass selected item to adapter
                selectedItemId = mStartId;
            }
        }
    }

    private void setToolbarTitleAndImage(int position) {
        //TODO: Handle errors here
        if (mCursor == null) {
            Log.e(LOG_TAG, "CURSOR IS NULL");
            return;
        }
        mCursor.moveToPosition(position);
        String title = mCursor.getString(ArticleLoader.Query.TITLE);
        Log.i(LOG_TAG, "Current title --> " + title);
        ctLayout.setTitle(title);

        String imageUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);
//
//        Picasso.get().load(imageUrl).into(heroIv,
//                PicassoPalette.with(imageUrl, heroIv)
//                        .use(PicassoPalette.Profile.VIBRANT)
//                        .intoCallBack(new PicassoPalette.CallBack() {
//                            @Override
//                            public void onPaletteLoaded(Palette palette) {
//                                // Get the returned color from the PicassoPalette library.
//                                int color = palette.getVibrantColor(
//                                        ContextCompat.getColor(mPager.getContext(), R.color.primary));
//
//                                // Return RGB values (we are replacing alpha, so no need for that.
//                                // Docs: https://developer.android.com/reference/android/graphics/Color
//                                // Note that we can't reliably use Color api methods, since minimum API is 19
//                                int R = (color >> 16) & 0xff;
//                                int G = (color >>  8) & 0xff;
//                                int B = (color      ) & 0xff;
//
//                                // Create a new base color with same values, but applying semi-opaque alpha value
//                                int alphaColor = (150 & 0xff) << 24 |
//                                        (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
//
//                                // Set generate color to titleView background.
//                                toolbar.setBackgroundColor(alphaColor);
//                            }
//                        }));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e(LOG_TAG, "On Load Finished called! Cursor: " + cursor);
        mCursor = cursor;
        mPagerAdapter.setCursor(mCursor);
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

//    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
//        if (itemId == mSelectedItemId) {
//            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//            updateUpButtonPosition();
//        }
//    }
//
//    private void updateUpButtonPosition() {
//        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
//        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
//    }
}
