package com.example.xyzreader.ui;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.adapters.ItemPagerAdapter;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

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
    @BindView(R.id.subtitle) TextView subtitleTV;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout ctLayout;
    @BindView(R.id.toolbar)Toolbar toolbar;

    private Cursor mCursor;
    private long mStartId;
    private long selectedItemId;

    private String imageUrl;

    private ItemPagerAdapter mPagerAdapter;
    private int selectedPosition;
    private int alphaColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
        }

        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        // This call is supported by API 19, so we don't need to worry about a version check here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new ItemPagerAdapter(getSupportFragmentManager(), mCursor);
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Log.i(LOG_TAG, "Selecting the current position --> " + position);
                bindDataToViews(position);

                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }

                selectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        if (getIntent() != null && getIntent().getData() != null) {
            if (savedInstanceState == null) {
                Log.e(LOG_TAG, "Retrieving URI <<-- " + getIntent().getData());

                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                //TODO: Pass selected item to adapter
                selectedItemId = mStartId;
            }
            selectedPosition = getIntent().getExtras().getInt("position");
            setStatusBarColor(getIntent().getExtras().getInt("alphaColor"));
            ctLayout.setBackgroundColor(getIntent().getExtras().getInt("vibrantColor"));

            //TODO: Remove log
            Log.e(LOG_TAG, "Passed Position from bundle --> " + selectedPosition);
        }
        //TODO: Remove logs
        Log.e(LOG_TAG, "Selected ITem ID: " + selectedItemId);
    }

    private void bindDataToViews(int position) {
        // Handle Error here
        if (mCursor == null) {
            return;
        }

        mCursor.moveToPosition(position);

        setSubtitle();
        setToolbarTitleAndImage();
    }

    private void setToolbarTitleAndImage() {
        //TODO: Handle errors here
        if (mCursor == null) {
            return;
        }
        String title = mCursor.getString(ArticleLoader.Query.TITLE);
        ctLayout.setTitle(title);

        imageUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);

        Picasso.get().load(imageUrl).noFade().into(heroIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        ArticleDetailActivity.this.supportStartPostponedEnterTransition();
                        //supportStartPostponedEnterTransition();

                        PicassoPalette.with(imageUrl, heroIv)
                                .use(PicassoPalette.Profile.VIBRANT)
                                .intoCallBack(new PicassoPalette.CallBack() {

                                    @Override
                                    public void onPaletteLoaded(Palette palette) {
                                        // Get the returned color from the PicassoPalette library.
                                        int toolbarColor = palette.getVibrantColor(
                                                ContextCompat.getColor(mPager.getContext(), R.color.primary));

                                        // Return RGB values (we are replacing alpha, so no need for that.)
                                        // https://developer.android.com/reference/android/graphics/Color
                                        // Note that we can't reliably use Color api methods, since minimum
                                        // API is 19
                                        int toolbarColor_R = (toolbarColor >> 16) & 0xff;
                                        int toolbarColor_G = (toolbarColor >> 8) & 0xff;
                                        int toolbarColor_B = (toolbarColor) & 0xff;

                                        // Create a new base color with same values, but applying
                                        // a semi-opaque alpha value
                                        int alphaColor = (150 & 0xff) << 24 |
                                                (toolbarColor_R & 0xff) << 16 |
                                                (toolbarColor_G & 0xff) << 8 |
                                                (toolbarColor_B & 0xff);

                                        // Set generate color to titleView background.
                                        toolbar.setBackgroundColor(ContextCompat.getColor(
                                                mPager.getContext(), R.color.transparent));

                                        ctLayout.setContentScrimColor(toolbarColor);
                                        ctLayout.setBackgroundColor(toolbarColor);
                                        setStatusBarColor(alphaColor);
                                    }
                                });
                    }

                    @Override
                    public void onError(Exception e) {
                        ArticleDetailActivity.this.supportStartPostponedEnterTransition();
                    }
                });
    }

    private void setSubtitle() {
        String subtitle = mCursor.getString(ArticleLoader.Query.AUTHOR);
        subtitleTV.setText(subtitle);
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

        if(selectedPosition == 0) {
            bindDataToViews(0);
        }

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

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}
