package com.example.xyzreader.ui.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.utils.TextRecyclerFeeder;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TextRecyclerAdapter extends RecyclerView.Adapter<TextRecyclerAdapter.ViewHolder> {
    private static final String LOG_TAG = TextRecyclerAdapter.class.toString();

    String textChunk;
    private TextRecyclerFeeder recyclerFeeder;

    public TextRecyclerAdapter(String inputString) {
        Log.e(LOG_TAG, "--> Notice confirmed. Setting up Adapter");
        this.textChunk = inputString;
        this.recyclerFeeder = new TextRecyclerFeeder(inputString);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.bind(recyclerFeeder.getTextForPosition(position));
        holder.bind(recyclerFeeder.getParagraph(position));
    }

    @Override
    public int getItemCount() {
        return recyclerFeeder.getParagraphCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_recycler_tv) TextView recyclerItemTV;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String itemText) {
            recyclerItemTV.setText(itemText);
        }

    }
}
