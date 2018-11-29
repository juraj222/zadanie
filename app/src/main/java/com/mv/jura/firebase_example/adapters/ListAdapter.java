package com.mv.jura.firebase_example.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mv.jura.firebase_example.Item;
import com.mv.jura.firebase_example.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;

//https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;
    private FirebaseFirestore db;

    public ListAdapter(Context context, ArrayList<Item> items) throws IOException {
        mContext = context;
        mItems = items;

        db = FirebaseFirestore.getInstance();
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
    public void onViewRecycled(ListAdapter.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (mItems.get(position) != null && mItems.get(position).getVideoPlayer() != null) {
            mItems.get(position).getVideoPlayer().release();
        }
        super.onViewRecycled(holder);
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
            ArrayList<Item> items;
            items = populateItems();


            RecyclerView mRecyclerView = itemView.findViewById(R.id.subRecyclerView);

            mAdapter = new SubListAdapter(mContext, items);
            mRecyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.scrollToPosition(1); // tu mozno bude chyba, ak nie je ziaden prispevok
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

    //tu sa naplni profil a prispevky profilu
    private ArrayList<Item> populateItems() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("fero", "10.1.2019", null, "5", null, null, true));
        items.add(new Item("fero", null, "12.1.2019", null, null, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4", false));
        items.add(new Item("fero", null, "13.1.2019", null, "http://i.imgur.com/e7MfwB0.jpg", null, false));
        return items;
    }

    private Item getItem() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("dw", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("dw", "Error adding document", e);
                    }
                });

        return null;
    }
}
