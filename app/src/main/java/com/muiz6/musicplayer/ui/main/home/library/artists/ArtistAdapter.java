package com.muiz6.musicplayer.ui.main.home.library.artists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowArtistBinding;
import com.muiz6.musicplayer.databinding.RowListHeaderBinding;
import com.muiz6.musicplayer.ui.main.home.library.TitledRecyclerAdapter;
import com.muiz6.musicplayer.ui.recyclerviewutil.DefaultViewHolder;

import java.util.List;

public class ArtistAdapter extends TitledRecyclerAdapter<DefaultViewHolder> {

	private final List<ArtistItemModel> _artistList;

	public ArtistAdapter(List<ArtistItemModel> artistList) {
		_artistList = artistList;
	}

	@NonNull
	@Override
	public DefaultViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
			@NonNull ViewGroup parent,
			int viewType) {
		if (viewType != R.layout.row_list_header) {
			final View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.row_artist, parent, false);
			return new DefaultViewHolder(view);
		}
		return super.onCreateViewHolder(inflater, parent, viewType);
	}

	@Override
	public void onBindViewHolder(@NonNull DefaultViewHolder holder, int position) {
		final int viewType = getItemViewType(position);
		if (viewType == R.layout.row_list_header) {
			final RowListHeaderBinding binding = RowListHeaderBinding.bind(holder.itemView);
			binding.getRoot().setText(R.string.all_artists);
		}
		else {
			final RowArtistBinding binding = RowArtistBinding.bind(holder.itemView);
			final ArtistItemModel model = _artistList.get(position);
			binding.rowArtistTitle.setText(model.getArtist());
			if (model.getAlbumCount() != ArtistItemModel.UNDEFINED) {
				binding.rowArtistAlbumCount.setText(String.valueOf(model.getAlbumCount()));
			}
			if (model.getSongCount() != ArtistItemModel.UNDEFINED) {
				binding.rowArtistSongCount.setText(String.valueOf(model.getSongCount()));
			}
		}
	}

	@Override
	public int getItemCount() {

		// plus one for title
		return _artistList.size() + 1;
	}
}
