package com.mv.jura.firebase_example;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;


import java.util.ArrayList;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mv.jura.firebase_example.adapters.ListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoggedActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    String userId;
    FirebaseFirestore db;
    Item userProfile;
    Boolean loaded = false;
    Intent postActivityIntent;
    final ArrayList<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        userId = getIntent().getSerializableExtra("userId").toString();
        initDB();
        ButterKnife.bind(this);
        populateProfiles();

        //plus tlacidlo
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postActivityIntent = new Intent(LoggedActivity.this, PostActivity.class);
                postActivityIntent.putExtra("userId", userId);
                //postActivityIntent.putExtra("loaded", loaded);
                postActivityIntent.putExtra("userProfile", (userProfile = getProfile(userId)));
            }
        });
        /*runOnUiThread(new Runnable() {
            public void run() {

                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ActualizeItems();
                    }
                }, 0, 5000); // kazdych 5sekund aktualizuje;
            }
        });*/
        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                if(items != null && items.size() > 0 && loaded) {
                    ActualizeItems();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);


    }
    public void ActualizeItems(){
        //Query query = db.collection("posts").orderBy("date"); //where date < items[0].getDate();
        Query query = db.collection("posts").whereGreaterThan("date", new Timestamp(items.get(0).getDateObject()) );
        //loaded = false;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean foundNew = false;
                    //int previousContentSize = items.size();
                    int insered = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        if(doc.getDate("date") == null){
                            break;
                        }
                        foundNew = true;
                        Item item = new Item(doc.getString("userid"));
                        item.setDate(doc.getDate("date").toString());
                        item.setDateObject(doc.getDate("date"));
                        items.add(0,item);
                        insered++;
                    }
                    if(foundNew) {
                        mRecyclerView.scrollToPosition(0); //ak tu toto nebude, tak sa dolava da iny prispevok, weird
                        mAdapter.notifyItemInserted(0);
                    }
                } else {
                    System.out.println();
                }
                //loaded = true;
            }
        });
    }

    private void populateProfiles() {
        getUserIds();
    }

    private void getUserIds(){
        //pole userId, ktory maju viac ako 0 prispevkov, zoradene podla casu posledneho prispevku // mozu sa opakovat

        Query query = db.collection("posts").orderBy("date", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Item item = new Item(doc.getString("userid"));
                        item.setDate(doc.getDate("date").toString());
                        item.setDateObject(doc.getDate("date"));
                        items.add(item);
                    }
                    run(items);
                } else {

                }
            }
        });
    }

    private Item getProfile(String userId){
        final Item item = new Item(userId);
        item.setProfile(true);
        DocumentReference user = db.collection("users").document(userId);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    item.setDate(doc.get("date").toString());
                    item.setName(doc.getString("username"));
                    item.setPostCount(doc.get("numberOfPosts").toString());
                    startActivity(postActivityIntent);
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

    private void initDB(){
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void run(ArrayList<Item> items) {
        mAdapter = new ListAdapter(getApplicationContext(), items);
        mRecyclerView.setAdapter(mAdapter);
        //mListView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);
        //https://stackoverflow.com/questions/32324926/swipe-one-item-at-a-time-recyclerview
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        loaded = true;
    }

    @Override
    public void onBackPressed() {
        items.clear();
        userId = null;
        startActivity(new Intent(LoggedActivity.this, LoginActivity.class));
        super.onBackPressed();
    }
}
