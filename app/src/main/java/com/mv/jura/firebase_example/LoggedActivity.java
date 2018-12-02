package com.mv.jura.firebase_example;

import android.content.Intent;
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
        items = new ArrayList<>();
        //tu sa naplni items tolkokrat kolko bude vedla seba profilov
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());
        items.add(new Item());
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
