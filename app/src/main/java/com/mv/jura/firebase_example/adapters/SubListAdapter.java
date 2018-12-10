package com.mv.jura.firebase_example.adapters;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mv.jura.firebase_example.Item;
import com.mv.jura.firebase_example.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

//https://www.learnhowtoprogram.com/android/web-service-backends-and-custom-fragments/custom-adapters-with-recyclerview
public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder>{
    private ArrayList<Item> mItems = new ArrayList<>();
    private Context mContext;
    private SimpleExoPlayer mPlayer;

    public SubListAdapter(Context context, ArrayList<Item> items, SimpleExoPlayer player) {
        mContext = context;
        mItems = items;
        mPlayer = player;
    }
    @Override
    public SubListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubListAdapter.ViewHolder holder, int position) {
        holder.bindItem(mItems.get(position), mPlayer);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.relativeLayoutProfile)
        RelativeLayout relativeLayoutProfile;
        @Bind(R.id.relativeLayoutImagePost)
        RelativeLayout relativeLayoutImagePost;
        @Bind(R.id.relativeLayoutVideoPost)
        RelativeLayout relativeLayoutVideoPost;

        @Bind(R.id.textViewProfileName)
        TextView textViewProfileName;
        @Bind(R.id.textViewProfileDate)
        TextView textViewProfileDate;
        @Bind(R.id.textViewProfilePostCount)
        TextView textViewProfilePostCount;

        @Bind(R.id.textViewImagePostName)
        TextView textViewImagePostName;
        @Bind(R.id.textViewImagePostDate)
        TextView textViewImagePostDate;
        @Bind(R.id.imageView)
        ImageView imageView;

        @Bind(R.id.textViewVideoPostName)
        TextView textViewVideoPostName;
        @Bind(R.id.textViewVideoPostDate)
        TextView textViewVideoPostDate;
        @Bind(R.id.playerView)
        PlayerView playerView;

        private Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindItem(Item item, SimpleExoPlayer player) {
            if(item.isProfile()){
                relativeLayoutProfile.setVisibility(View.VISIBLE);
                relativeLayoutVideoPost.setVisibility(View.INVISIBLE);
                relativeLayoutImagePost.setVisibility(View.INVISIBLE);
                textViewProfileName.setText(item.getName());
                textViewProfileDate.setText(item.getRegistrationDateFormated());
                textViewProfilePostCount.setText(item.getPostCount());
            }else{
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    relativeLayoutProfile.setVisibility(View.INVISIBLE);
                    relativeLayoutVideoPost.setVisibility(View.INVISIBLE);
                    relativeLayoutImagePost.setVisibility(View.VISIBLE);
                    textViewImagePostName.setText(item.getName());
                    textViewImagePostDate.setText(item.getDateFormated());

                    Picasso.get().setLoggingEnabled(true);
                    Picasso.get()
                            .load(item.getImageUrl())
                            .placeholder(R.drawable.common_full_open_on_phone)
                            .error(R.drawable.ic_launcher_background)
                            .fit()
                            .centerInside()
                            .into(imageView);

                } else {
                    relativeLayoutProfile.setVisibility(View.INVISIBLE);
                    relativeLayoutImagePost.setVisibility(View.INVISIBLE);
                    relativeLayoutVideoPost.setVisibility(View.VISIBLE);
                    textViewVideoPostName.setText(item.getName());
                    textViewVideoPostDate.setText(item.getDateFormated());

                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                            Util.getUserAgent(mContext, "yourApplicationName"));
                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(item.getVideoUrl()));

                    player.prepare(videoSource);
                    playerView.setPlayer(player);
                }
            }
        }
    }
}
