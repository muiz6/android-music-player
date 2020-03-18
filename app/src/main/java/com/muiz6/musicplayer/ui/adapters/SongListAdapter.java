package com.muiz6.musicplayer.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.services.AudioService;

import java.util.ArrayList;
import java.util.Objects;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle;
        public TextView songArtist;
        public LinearLayout songWidget;
        public MyViewHolder(View view) {
            super(view);

            this.songWidget = view.findViewById(R.id.widget_song_item);
            this.songTitle = view.findViewById(R.id.widget_song_item_title);
            this.songArtist = view.findViewById(R.id.widget_song_item_artist);
        }
    }

    private ArrayList<SongDataModel> mSongList;

    public SongListAdapter() {

        // initialize with empty list
        mSongList = new ArrayList<>();
    }

    // @Override
    // public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    //     holder.songTitle.setText(mSongDataViewModel.getSongList().getValue().get(position).getTitle());
    //     holder.songArtist.setText(mSongDataViewModel.getSongList().getValue().get(position).getArtist());
    // }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TextView songTitle = parent.findViewById(R.id.widget_song_item_title);
        // TextView songArtist =  parent.findViewById(R.id.widget_song_item_artist);
        // LinearLayout layout = parent.findViewById(R.id.widget_song_item);
        // MyViewHolder vh = new MyViewHolder(layout, songTitle, songArtist);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_song_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(v.getContext(), AudioService.class);
                // intent.putExtra(mSongList.get(0).getPath(), 0);
                // intent.getAction();
            }
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songTitle.setText(mSongList.get(position).getTitle());
        holder.songArtist.setText(mSongList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    // copied from:
    // https://github.com/hazems/mvvm-sample-app/blob/part1/app/src/main/java/com/example/test/mvvmsampleapp/view/adapter/ProjectAdapter.java
    // TODO: try to understand this
    public void setSongList(final ArrayList<SongDataModel> songList) {
        if (mSongList == null) {
            mSongList = songList;
            notifyDataSetChanged();

            // producing error for some reason
            // notifyItemRangeInserted(0, songList.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return SongListAdapter.this.mSongList.size();
                }

                @Override
                public int getNewListSize() {
                    return songList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return SongListAdapter.this.mSongList.get(oldItemPosition).getTitle()
                            .equals(songList.get(newItemPosition).getTitle());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    SongDataModel project = songList.get(newItemPosition);
                    SongDataModel old = songList.get(oldItemPosition);
                    return project.getTitle().equals(old.getTitle())
                            && Objects.equals(project.getArtist(), old.getArtist())
                            && Objects.equals(project.getPath(), old.getPath());
                }
            });
            mSongList = songList;
            result.dispatchUpdatesTo(this);
        }
    }
}
