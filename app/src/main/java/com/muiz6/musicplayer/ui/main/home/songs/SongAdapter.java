package com.muiz6.musicplayer.ui.main.home.songs;

import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowSongItemBinding;
import com.muiz6.musicplayer.databinding.RowSongItemFirstBinding;

import java.util.ArrayList;
import java.util.Objects;

public class SongAdapter extends RecyclerView.Adapter {

	private final MediaBrowserCompat _mediaBrowser;
	private MediaControllerCompat _mediaController;
	private ArrayList<MediaBrowserCompat.MediaItem> _songList;

	public SongAdapter(ArrayList<MediaBrowserCompat.MediaItem> list,
			MediaBrowserCompat mediaBrowser) {
		_songList = list;
		_mediaBrowser = mediaBrowser;
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
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_mediaController == null) {
					try {
						_mediaController = new MediaControllerCompat(context,
								_mediaBrowser.getSessionToken());
					}
					catch (RemoteException e) {
						Log.d("SongFragment", "Failed to initialize media controller", e);
					}
				}
				int position = holder.getAdapterPosition();

				// -1 to bring recyclerview items and media items in sync
				int toPlay = position - 1;
				_mediaController.getTransportControls()
						.playFromMediaId(_songList.get(toPlay).getMediaId(), null);
			}
		});
		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
		if (position == 0) {
			final RowSongItemFirstBinding binding = RowSongItemFirstBinding.bind(holder.itemView);
			binding.getRoot()
					.setText(holder.itemView.getContext().getString(R.string.all_songs));
		}
		else {
			final RowSongItemBinding binding = RowSongItemBinding.bind(holder.itemView);
			final TextView songTitle = binding.rowSongItemTitle;
			final TextView songArtist = binding.rowSongItemArtist;

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
					return SongAdapter.this._songList.size();
				}

				@Override
				public int getNewListSize() {
					return songList.size();
				}

				@Override
				public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
					return SongAdapter.this._songList.get(oldItemPosition)
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

			// this statement is causing recyclerview to scroll to the bottom
			// so using notifyDataSetChanged() instead
			// result.dispatchUpdatesTo(this);
			notifyDataSetChanged();
		}
	}
}
