package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.adapters.ItemPagerAdapter;
import com.example.xyzreader.utils.HelperUtils;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity class representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ArticleDetailActivity.class.toString();

    @BindView(R.id.view_pager) ViewPager mPager;
    @BindView(R.id.hero_image)ImageView heroIv;
    @BindView(R.id.subtitle) TextView subtitleTV;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout ctLayout;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.share_fab)FloatingActionButton shareFab;

    private Cursor mCursor;
    private long mStartId;
    private long selectedItemId;

    private ItemPagerAdapter mPagerAdapter;
    private int selectedPosition;

    String articleTitle;
    String articleAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        // Set-up toolbar, ActionBar and related functions.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportLoaderManager().initLoader(0, null, this);

        // Set-up the view pager adapter.
        mPagerAdapter = new ItemPagerAdapter(getSupportFragmentManager(), mCursor);
        mPager.setAdapter(mPagerAdapter);

        // Set-up listener to detect page changes so that we can handle the cursor correctly.
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

        // Get intent data from our main activity.
        // TODO: Set up loading from saved state correctly.
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
        }

    }

    /**
     * Binds our cursor data to the required views.
     * @param position The position to move the cursor to respectively.
     */
    private void bindDataToViews(int position) {
        // Handle Error here
        if (mCursor == null) {
            Log.e(LOG_TAG, "There was a problem retrieving the cursor. Check implementation");
            return;
        }

        mCursor.moveToPosition(position);

        setSubtitle();
        setToolbarTitleAndImage();
        setUpFab();
    }

    /**
     * Simple method that set's up the ArticleDetailActivity FAB button.
     */
    private void setUpFab() {
        final StringBuilder shareIntentText = new StringBuilder()
                .append(this.getString(R.string.share_text_name)).append(" ")
                .append(articleTitle).append(" ")
                .append(this.getString(R.string.share_text_author)).append(" ")
                .append(articleAuthor).append(".");

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareIntentText.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,
                        view.getContext().getString(R.string.share_dialogue_title)));
            }
        });
    }

    /**
     * Simple method responsible for setting up and biding toolbar data and header image to UI.
     */
    private void setToolbarTitleAndImage() {
        articleTitle = mCursor.getString(ArticleLoader.Query.TITLE);
        ctLayout.setTitle(articleTitle);

        final String imageUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);

        Picasso.get().load(imageUrl).into(heroIv, PicassoPalette.with(imageUrl, heroIv)
                .use(PicassoPalette.Profile.VIBRANT)
                .intoCallBack(new PicassoPalette.CallBack() {
                    @Override
                    public void onPaletteLoaded(Palette palette) {
                        // Get the returned color from the PicassoPalette library.
                        int baseColor = palette.getVibrantColor(
                                ContextCompat.getColor(mPager.getContext(), R.color.primary));

                        // Set generate color to titleView background.
                        toolbar.setBackgroundColor(ContextCompat.getColor(
                                mPager.getContext(),R.color.transparent));

                        ctLayout.setContentScrimColor(baseColor);
                        ctLayout.setBackgroundColor(baseColor);
                        setStatusBarColor(HelperUtils.generateSemiOpaque(baseColor, 150));
                    }
                })
        );
    }

    /**
     * Binds subtitle data to ui.
     */
    private void setSubtitle() {
        articleAuthor = mCursor.getString(ArticleLoader.Query.AUTHOR);
        subtitleTV.setText(articleAuthor);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
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
    public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}