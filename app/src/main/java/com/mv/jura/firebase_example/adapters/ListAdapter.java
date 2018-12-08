package com.mv.jura.firebase_example.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Date;
import com.mv.jura.firebase_example.Item;
import com.mv.jura.firebase_example.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

//https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;
    private SimpleExoPlayer mPlayer;
    private FirebaseFirestore db;
    private int indexOfUserIds;

    private SubListAdapter mAdapter;

    public ListAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        mItems = items;
        indexOfUserIds = 0;
        initDB();
        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
        mPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        mPlayer.setPlayWhenReady(true);
    }
    private void initDB(){
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
    //https://stackoverflow.com/questions/33316837/how-to-prevent-items-from-getting-duplicated-when-scrolling-recycler-view
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        //@Bind(R.id.itemTextView) TextView itemTextView;

        private Context mContext;
        private ArrayList<Item> items;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mContext = itemView.getContext();
            //setIsRecyclable(false);
            items = populateItems(mItems.get(indexOfUserIds++), new LoadedChecker(), this);

        }
        private void run(){
            RecyclerView mRecyclerView = itemView.findViewById(R.id.subRecyclerView);
            mAdapter = new SubListAdapter(mContext, items, mPlayer);
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
    private ArrayList<Item> populateItems(Item actualItem, LoadedChecker loadedChecker, ViewHolder viewHolder) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(getProfile(actualItem.getUserId(), loadedChecker, viewHolder)); //prida sa profil
        getPosts(actualItem,items, loadedChecker, viewHolder); // pridaju sa prispevky pouzivatela
        return items;
//        items.add(new Item("fero","10.1.2019", null,"5",null,null,true));
//        items.add(new Item("fero",null, "12.1.2019",null,null,"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",false));
//        items.add(new Item("fero",null, "13.1.2019",null,"http://i.imgur.com/e7MfwB0.jpg",null,false));
        //items = getItems("user2");
//        addItem(new Item("fero","10.1.2019", null,"5",null,null,true));
//        createRegistration("pokus2", "user2");
//        createpost(PostType.image, "", "http://i.imgur.com/e7MfwB0.jpg", "pokus2", Calendar.getInstance(), "user2");
//        createpost(PostType.video, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4", "", "pokus2", Calendar.getInstance(), "user2");
    }
    private Item getProfile(String userId, final LoadedChecker loadedChecker, final ViewHolder viewHolder){
        final Item item = new Item();
        item.setProfile(true);
        // tu sa naplnia z DB tieto 3 stringy
        DocumentReference user = db.collection("users").document(userId);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    item.setDate(doc.get("date").toString());
                    item.setName(doc.getString("username"));
                    item.setPostCount(doc.get("numberOfPosts").toString());
                    item.setProfile(true);
                    //mAdapter.notifyDataSetChanged();
                    loadedChecker.setProfileLoaded(true);
                    if(loadedChecker.getPostsLoaded()){
                        viewHolder.run();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return item;
    }
    private void getPosts(Item actualItem, final ArrayList<Item> items, final LoadedChecker loadedChecker, final ViewHolder viewHolder){
        // tu sa ziskaju posty z DB a pridaju sa do items, zoradene maju byt podla casu prispevku
        // items.add(new Item("fero",null, "13.1.2019",null,"http://i.imgur.com/e7MfwB0.jpg",null,false));
        Query query = db.collection("posts").whereLessThanOrEqualTo("date", new Timestamp(actualItem.getDateObject()) ).whereEqualTo("userid", actualItem.getUserId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        items.add(1,new Item( doc.getString("username"), null, ((Timestamp)doc.get("date")).toDate().toString(), null,doc.get("imageurl").toString(),doc.get("videourl").toString(),false));
                    }

                    loadedChecker.setPostsLoaded(true);
                    if(loadedChecker.getProfileLoaded()){
                        viewHolder.run();
                    }
                    //mAdapter.notifyDataSetChanged();
                } else {
                    System.out.println();
                }
            }
        });
    }
}
