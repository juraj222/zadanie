package com.mv.jura.firebase_example;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    public ArrayList<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        ButterKnife.bind(this);
        items = populateProfiles();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoggedActivity.this, PostActivity.class));
            }
        });
        run();
    }

    private ArrayList<Item> populateProfiles() {
        items = getUserIds();
        /*for (String userId: getUserIds()) {
            items.add(new Item(userId));
        }*/
        return items;
    }

    private ArrayList<Item> getUserIds(){
        //pole userId, ktory maju viac ako 0 prispevkov, zoradene podla casu posledneho prispevku // mozu sa opakovat
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final ArrayList<Item> items = new ArrayList<>();
        Query query = db.collection("posts");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        items.add( new Item(doc.getString("userid")));
                    }
                } else {

                }
            }
        });

        return items;
    }

    public void run() {
        mAdapter = new ListAdapter(getApplicationContext(), items);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        //https://stackoverflow.com/questions/32324926/swipe-one-item-at-a-time-recyclerview
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
    }
}
