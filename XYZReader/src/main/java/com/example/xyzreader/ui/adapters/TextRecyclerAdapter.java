package com.example.xyzreader.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.utils.TextRecyclerFeeder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextRecyclerAdapter extends RecyclerView.Adapter<TextRecyclerAdapter.ViewHolder> {
    private static final String LOG_TAG = TextRecyclerAdapter.class.toString();

    String textChunk;
    private TextRecyclerFeeder recyclerFeeder;

    public TextRecyclerAdapter(String textChunk) {
        Log.e(LOG_TAG, "--> Notice confirmed. Setting up Adapter");
        this.textChunk = textChunk;
        this.recyclerFeeder = new TextRecyclerFeeder(textChunk);
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

        holder.bind(recyclerFeeder.getTextForPosition(position));
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_recycler_tv) TextView recyclerItemTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String itemText) {
            recyclerItemTV.setText(itemText);
        }

    }
}
