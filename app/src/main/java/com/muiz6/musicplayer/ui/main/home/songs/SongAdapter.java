package com.muiz6.musicplayer.ui.main.home.songs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowSongItemBinding;
import com.muiz6.musicplayer.databinding.RowSongItemFirstBinding;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter {

	private List<SongItemModel> _songList;

	public SongAdapter(List<SongItemModel> list) {
		_songList = list;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		final Context context = parent.getContext();
		final LayoutInflater inflator = LayoutInflater.from(context);
		View view;
		if (viewType == 0) {
			view = inflator.inflate(R.layout.row_song_item_first, parent, false);
		}
		else {
			view = inflator.inflate(R.layout.row_song_item, parent, false);
		}
		final RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view) {
		};
		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
		if (position == 0) {
			final RowSongItemFirstBinding binding = RowSongItemFirstBinding.bind(holder.itemView);
			binding.getRoot().setText(holder.itemView.getContext().getString(R.string.all_songs));
		}
		else {
			final RowSongItemBinding binding = RowSongItemBinding.bind(holder.itemView);
			final TextView songTitle = binding.rowSongItemTitle;
			final TextView songArtist = binding.rowSongItemArtist;

			// -1 bcz index 0 is occupied by header and item positions are no longer in sync
			final int syncedPosition = position - 1;
			songTitle.setText(_songList.get(syncedPosition).getTitle());
			songArtist.setText(_songList.get(syncedPosition).getArtist());
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

	public void setSongList(final List<SongItemModel> songList) {

		// todo: optimize this with diffutil
		_songList = songList;

		// this statement is causing recyclerview to scroll to the bottom
		// so using notifyDataSetChanged() instead
		// result.dispatchUpdatesTo(this);
		notifyDataSetChanged();

		// producing error for some reason
		// notifyItemRangeInserted(0, songList.size());
	}
}
