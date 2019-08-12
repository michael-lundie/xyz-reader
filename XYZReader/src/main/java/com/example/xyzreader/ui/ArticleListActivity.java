package com.example.xyzreader.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.animation.GridLayoutAnimationController;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.ui.adapters.ItemListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.widget.SwipeRefreshLayout.*;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ArticleListActivity.class.toString();

    private boolean mIsRefreshing = false;

    @BindView(R.id.main_activity) CoordinatorLayout mainLayout;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.list_recycler_view) RecyclerView listRecyclerView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    ItemListAdapter listAdapter;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                Log.e(LOG_TAG, "Refreshing Status --> " + mIsRefreshing);
                updateRefreshingUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(LOG_TAG, "onRefresh called.");
                refresh();
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            Log.i(LOG_TAG, "Saved instance state was null.");
            refresh();
        } else {
            Log.i(LOG_TAG, "Saved instance state is NOT null.");
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private void updateRefreshingUI() {
        swipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        listAdapter = new ItemListAdapter(cursor,
                new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Uri uri, int position, int alphaColor,
                                    int vibrantColor) {
                //TODO: Remove LOG
                Log.e(LOG_TAG, "Requesting URI --> " + uri);

                Intent detailActivityIntent = new Intent(Intent.ACTION_VIEW, uri);
                detailActivityIntent.putExtra("position", position)
                                    .putExtra("alphaColor", alphaColor)
                                    .putExtra("vibrantColor", vibrantColor);
                startActivity(detailActivityIntent);
            }
        });

        listAdapter.setHasStableIds(true);
        listRecyclerView.setAdapter(listAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, columnCount);

        listRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listRecyclerView.setAdapter(null);
    }
}