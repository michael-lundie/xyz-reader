package com.example.xyzreader.ui.adapters;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.ui.ArticleDetailFragment;

public class ItemPagerAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = ItemPagerAdapter.class.toString();

    Cursor cursor;

    public ItemPagerAdapter(FragmentManager fragmentManager, Cursor cursor) {
        super(fragmentManager);
        this.cursor = cursor;
        //TODO: Remove LOG
        Log.e(LOG_TAG, "ItemPagerAdapter created. Cursor: " + cursor);
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public Fragment getItem(int position) {
        //TODO: Remove LOG
        Log.e(LOG_TAG, "getItem called. Position -->> " + position);
        cursor.moveToPosition(position);
        return ArticleDetailFragment.newInstance(cursor.getLong(ArticleLoader.Query._ID));
    }

    @Override
    public int getCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //TODO: Remove LOG
        Log.e(LOG_TAG, "DATA SET CHANGED: " + cursor);
    }
}