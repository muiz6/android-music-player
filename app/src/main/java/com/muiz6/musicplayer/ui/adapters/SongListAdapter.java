package com.muiz6.musicplayer.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.Repository;
import com.muiz6.musicplayer.models.SongDataModel;

import java.util.ArrayList;
import java.util.Objects;

public class SongListAdapter extends RecyclerView.Adapter {

    private ArrayList<SongDataModel> _songList;

    public SongListAdapter() {

        // initialize with empty list
        _songList = new ArrayList<>();
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
        return new RecyclerView.ViewHolder(view) {};
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
            final View clickable = holder.itemView.findViewById(R.id.row_song_item_clickable);

            // -1 bcz index 0 is occupied by header and item positions are no longer in sync
            songTitle.setText(_songList.get(position - 1).getTitle());
            songArtist.setText(_songList.get(position - 1).getArtist());

            // TODO: get rid of this abomination ;C
            clickable.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SongDataModel data = Repository.getInstance(clickable.getContext())
                        .getSongList().getValue().get(position - 1);

                    MediaControllerCompat.getMediaController(_getActivity(clickable))
                        .getTransportControls()
                        .playFromUri(Uri.parse(data.getPath()), null);

                }
            });
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
    public void setSongList(final ArrayList<SongDataModel> songList) {
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
                    return SongListAdapter.this._songList.size();
                }

                @Override
                public int getNewListSize() {
                    return songList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return SongListAdapter.this._songList.get(oldItemPosition).getTitle()
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
            _songList = songList;
            result.dispatchUpdatesTo(this);
        }
    }

    private Activity _getActivity(View v) {
        Context context = v.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
