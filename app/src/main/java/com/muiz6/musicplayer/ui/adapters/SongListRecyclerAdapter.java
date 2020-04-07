package com.muiz6.musicplayer.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;

import java.util.ArrayList;
import java.util.Objects;

public class SongListRecyclerAdapter extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<MediaBrowserCompat.MediaItem> _songList;
    private OnItemClickListener _clickListener;

    public SongListRecyclerAdapter(ArrayList<MediaBrowserCompat.MediaItem> list,
                                   OnItemClickListener listener) {

        // initialize with empty list
        _songList = list;
        _clickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflator = LayoutInflater.from(parent.getContext());

        View view;
        if (viewType == 0) {
            view = inflator.inflate(R.layout.row_song_item_first, parent, false);
        }
        else {
            view = inflator.inflate(R.layout.row_song_item, parent, false);
        }
        final RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view) {};
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _clickListener.onItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            TextView header = holder.itemView.findViewById(R.id.row_song_item_first_header);
            header.setText(holder.itemView.getContext().getString(R.string.tab_song_header));
        }
        else {
            TextView songTitle = holder.itemView.findViewById(R.id.row_song_item_title);
            TextView songArtist = holder.itemView.findViewById(R.id.row_song_item_artist);

            // -1 bcz index 0 is occupied by header and item positions are no longer in sync
            songTitle.setText(_songList.get(position - 1).getDescription().getTitle());
            songArtist.setText(_songList.get(position - 1).getDescription().getSubtitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {

        // +1 bcz of additional header item
        return _songList.size() + 1;
    }

    // copied from:
    // https://github.com/hazems/mvvm-sample-app/blob/part1/app/src/main/java/com/example/test/mvvmsampleapp/view/adapter/ProjectAdapter.java
    // TODO: try to understand this
    public void setSongList(final ArrayList<MediaBrowserCompat.MediaItem> songList) {
        if (_songList == null) {
            _songList = songList;
            notifyDataSetChanged();

            // producing error for some reason
            // notifyItemRangeInserted(0, songList.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return SongListRecyclerAdapter.this._songList.size();
                }

                @Override
                public int getNewListSize() {
                    return songList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return SongListRecyclerAdapter.this._songList.get(oldItemPosition)
                            .getDescription().getTitle()
                            .equals(songList.get(newItemPosition).getDescription().getSubtitle());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    MediaBrowserCompat.MediaItem project = songList.get(newItemPosition);
                    MediaBrowserCompat.MediaItem old = songList.get(oldItemPosition);
                    return project.getDescription().getTitle()
                            .equals(old.getDescription().getTitle())
                            && Objects.equals(project.getDescription().getSubtitle(),
                            old.getDescription().getSubtitle());
                }
            });
            _songList = songList;
            result.dispatchUpdatesTo(this);
        }
    }
}
