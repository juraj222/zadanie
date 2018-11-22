package com.mv.jura.firebase_example.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mv.jura.firebase_example.Item;
import com.mv.jura.firebase_example.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

//https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;

    public ListAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        mItems = items;
    }
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        holder.bindItem(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        //@Bind(R.id.itemTextView) TextView itemTextView;

        private Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            SubListAdapter mAdapter;
            ArrayList<Item> items = new ArrayList<>();
            items.add(new Item("hello"));
            items.add(new Item("world"));
            items.add(new Item("martin"));
            items.add(new Item("zuzana"));

            RecyclerView mRecyclerView = itemView.findViewById(R.id.subRecyclerView);

            mAdapter = new SubListAdapter(mContext, items);
            mRecyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            //https://stackoverflow.com/questions/32324926/swipe-one-item-at-a-time-recyclerview
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);

            ButterKnife.bind(this, itemView);

        }

        public void bindItem(Item item) {
            //itemTextView.setText(item.getName());
        }
    }
}