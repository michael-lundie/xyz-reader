package com.example.xyzreader.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.utils.FormatUtils;
import com.example.xyzreader.utils.TextRecyclerFeeder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = TextRecyclerAdapter.class.toString();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    String textChunk;
    String date;

    private TextRecyclerFeeder recyclerFeeder;

    public TextRecyclerAdapter(String inputString, String articleDate) {
        Log.e(LOG_TAG, "--> Notice confirmed. Setting up Adapter");
        this.textChunk = inputString;
        this.date = articleDate;
        this.recyclerFeeder = new TextRecyclerFeeder(inputString);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_recycler_item, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_date_header, parent, false);
            return new HeaderViewHolder(headerView);
        }

        throw new RuntimeException("There is a problem with the ViewType: " + viewType
                + " There is no match. Check view type use.");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.bind(recyclerFeeder.getParagraph(position));
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.bind(FormatUtils.formatDate(date));
        }
    }

    /**
     * Note that we are adding one to the item count in order to compensate for the addition
     * of the header item.
     * @return Item count plus 1
     */
    @Override
    public int getItemCount() {
        return recyclerFeeder.getParagraphCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // If the position is 0, we will return a header type.
        // Otherwise return a standard view.
        if(position == 0) {
            return VIEW_TYPE_HEADER;
        } return VIEW_TYPE_ITEM;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_recycler_tv) TextView recyclerItemTV;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String itemText) {
            recyclerItemTV.setText(itemText);
        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.date_recycler_header) TextView dateTV;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String dateText) { dateTV.setText(dateText);}
    }
}
