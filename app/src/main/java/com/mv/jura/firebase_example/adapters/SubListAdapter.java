package com.mv.jura.firebase_example.adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mv.jura.firebase_example.Item;
import com.mv.jura.firebase_example.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

//https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder>{
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;

    public SubListAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        mItems = items;
    }
    @Override
    public SubListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubListAdapter.ViewHolder holder, int position) {
        holder.bindItem(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.itemTextView)
        TextView itemTextView;

        private Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindItem(Item item) {
            itemTextView.setText(item.getName());
        }
    }
}
