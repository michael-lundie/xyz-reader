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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.adapters.ItemPagerAdapter;
import com.example.xyzreader.utils.HelperUtils;
import com.example.xyzreader.utils.Keys;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Callback;
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

    private String imageUrl;

    private ItemPagerAdapter mPagerAdapter;
    private int selectedPosition;

    String articleTitle;
    String articleAuthor;

    int statusBarColor;
    int backgroundColor;

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

        // Set-up toolbar, ActionBar and related functions.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Set-up the view pager adapter.
        mPagerAdapter = new ItemPagerAdapter(getSupportFragmentManager(), mCursor);
        mPager.setAdapter(mPagerAdapter);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                selectedItemId = mStartId;
                selectedPosition = getIntent().getIntExtra(Keys.POSITION, 0);
                statusBarColor = getIntent().getIntExtra(Keys.STATUS_BAR_COLOR, R.color.primary);
                backgroundColor = getIntent().getIntExtra(Keys.FADE_COLOR, R.color.primary);
                setPaletteColors(backgroundColor);
            }


            getSupportLoaderManager().initLoader(0, null, this);

        } else {
            // Saved instance state is not null.
            loadSavedStateFromBundle(savedInstanceState);
        }

        // Set-up listener to detect page changes so that we can handle the cursor correctly.
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bindDataToViews(position);

                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }

                selectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void loadSavedStateFromBundle(Bundle savedInstanceState) {
        selectedPosition = savedInstanceState.getInt(Keys.POSITION);
        statusBarColor = savedInstanceState.getInt(Keys.STATUS_BAR_COLOR);
        backgroundColor = savedInstanceState.getInt(Keys.FADE_COLOR);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Keys.POSITION, selectedPosition);
        outState.putInt(Keys.STATUS_BAR_COLOR, statusBarColor);
        outState.putInt(Keys.FADE_COLOR, backgroundColor);
        super.onSaveInstanceState(outState);
    }

    /**
     * Binds our cursor data to the required views.
     * @param position The position to move the cursor to respectively.
     */
    private void bindDataToViews(int position) {
        // Handle Error here
        if (mCursor == null) {
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
                        ArticleDetailActivity.this.supportStartPostponedEnterTransition();
                        //supportStartPostponedEnterTransition();

                        // Get the returned color from the PicassoPalette library.
                        int baseColor = palette.getVibrantColor(
                                ContextCompat.getColor(mPager.getContext(), R.color.primary));

                        setPaletteColors(baseColor);
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
        mCursor = cursor;
        mPagerAdapter.setCursor(mCursor);
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    bindDataToViews(position);
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

    /**
     * Override default ActionBar back behavior. Default behavior fires activity onCreate()
     * wth null saved instance - not a desired behavior.
     * @param item reference id of MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } return super.onOptionsItemSelected(item);
    }

    private void setPaletteColors(int baseColor) {
        // Set generate color to titleView background.
        toolbar.setBackgroundColor(ContextCompat.getColor(
                mPager.getContext(),R.color.transparent));

        ctLayout.setContentScrimColor(baseColor);
        ctLayout.setBackgroundColor(baseColor);
        setStatusBarColor(HelperUtils.generateSemiOpaque(baseColor, 150));
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}