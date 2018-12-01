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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.mv.jura.firebase_example.Item;
import com.mv.jura.firebase_example.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

//https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;
    private FirebaseFirestore db;

    public ListAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        mItems = items;
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
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
//        items.add(new Item("fero","10.1.2019", null,"5",null,null,true));
        //items.add(new Item("fero",null, "12.1.2019",null,null,"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",false));
        //items.add(new Item("fero",null, "13.1.2019",null,"http://i.imgur.com/e7MfwB0.jpg",null,false));
        items = getData();
//        addItem(new Item("fero","10.1.2019", null,"5",null,null,true));
//        createRegistration("pokus2", "user2");
//        createpost(PostType.image, "", "http://i.imgur.com/e7MfwB0.jpg", "pokus2", Calendar.getInstance(), "user2");
        return items;
    }

    private ArrayList<Item> getData(){
        final ArrayList<Item> items = new ArrayList<>();
        DocumentReference user = db.collection("users").document("Pokus");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    items.add(new Item( doc.getString("username"),doc.get("date").toString(), null,doc.get("numberOfPosts").toString(),null,null,true));
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        return items;
    }

    private void createpost(PostType type, String videourl, String imageurl, String username, Calendar date, String userid){
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("type", type.toString());
        newItem.put("videourl", videourl);
        newItem.put("imageurl", imageurl);
        newItem.put("username", username);
        newItem.put("userid", userid);
        newItem.put("date",  new SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getInstance().getTime()));
        db.collection("posts").document().set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void createRegistration(String name, String userId){
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("username", name);
        newItem.put("date",  new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
        newItem.put("numberOfPosts", 0);
        db.collection("users").document(userId).set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void UpdateRegistration(int numberOfPosts, String userId) {
        DocumentReference contact = db.collection("users").document(userId);
        contact.update("numberOfPosts", numberOfPosts)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }
}
