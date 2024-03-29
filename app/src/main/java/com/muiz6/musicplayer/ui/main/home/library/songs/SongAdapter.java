package com.muiz6.musicplayer.ui.main.home.library.songs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowListHeaderBinding;
import com.muiz6.musicplayer.databinding.RowSongBinding;
import com.muiz6.musicplayer.ui.main.home.library.TitledRecyclerAdapter;
import com.muiz6.musicplayer.ui.recyclerviewutil.DefaultViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SongAdapter extends TitledRecyclerAdapter<DefaultViewHolder> {

	@NonNull private List<SongItemModel> _songList;

	public SongAdapter(@NonNull List<SongItemModel> list) {
		_songList = list;
	}

	@NonNull
	@Override
	public DefaultViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
			@NonNull ViewGroup parent,
			int viewType) {
		if (viewType != R.layout.row_list_header) {
			return new DefaultViewHolder(inflater.inflate(R.layout.row_song, parent, false));
		}
		return super.onCreateViewHolder(inflater, parent, viewType);
	}

	@Override
	public void onBindViewHolder(@NonNull DefaultViewHolder holder, final int position) {
		final int viewType = getItemViewType(position);
		if (viewType == R.layout.row_list_header) {
			final RowListHeaderBinding binding = RowListHeaderBinding.bind(holder.itemView);
			binding.getRoot().setText(holder.itemView.getContext().getString(R.string.all_songs));
		}
		else {
			final RowSongBinding binding = RowSongBinding.bind(holder.itemView);
			final TextView songTitle = binding.rowSongItemTitle;
			final TextView songArtist = binding.rowSongItemArtist;

			// -1 bcz index 0 is occupied by header and item positions are no longer in sync
			final int syncedPosition = position - 1;
			final SongItemModel model = _songList.get(syncedPosition);
			songTitle.setText(model.getTitle());
			songArtist.setText(model.getArtist());
			binding.rowSongItemDuration.setText(_millisecondToString(model.getDuration()));

			// set album art
			final Bitmap albumArt = model.getAlbumArt();
			if (albumArt != null) {
				binding.rowSongItemIcon.setImageBitmap(model.getAlbumArt());
				binding.rowSongItemIcon.setScaleType(ImageView.ScaleType.FIT_XY);
				binding.rowSongItemIconContainer.setStrokeWidth(0);
			}
			else {

				// reset default drawable for other indices
				final Context context = binding.getRoot().getContext();
				final Drawable drawable = ContextCompat.getDrawable(context,
						R.drawable.ic_music_note);
				binding.rowSongItemIcon.setImageDrawable(drawable);
				binding.rowSongItemIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				final int dim = context.getResources().getDimensionPixelSize(R.dimen.stroke_m);
				binding.rowSongItemIconContainer.setStrokeWidth(dim);
			}

			// highlight active item
			if (model.isActive()) {
				binding.getRoot().setActivated(true);
			}
			else {
				binding.getRoot().setActivated(false);
			}
		}
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

	private static String _millisecondToString(int milli) {
		// milli = milli / 1000;
		// int h = milli/60;
		// int m = milli % 60;
		// return "" + h + ":" + m;
		final SimpleDateFormat format = new SimpleDateFormat("mm:ss");
		return format.format(new Date(milli));
	}
}
