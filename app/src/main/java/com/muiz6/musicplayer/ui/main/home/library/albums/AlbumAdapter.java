package com.muiz6.musicplayer.ui.main.home.library.albums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.TileAlbumBinding;
import com.muiz6.musicplayer.ui.recyclerviewutil.DefaultViewHolder;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<DefaultViewHolder> {

	private final List<AlbumItemModel> _albumList;

	public AlbumAdapter(List<AlbumItemModel> albumList) {
		_albumList = albumList;
	}

	@NonNull
	@Override
	public DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.tile_album, parent, false);
		return new DefaultViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull DefaultViewHolder holder, int position) {
		final TileAlbumBinding binding = TileAlbumBinding.bind(holder.itemView);
		final AlbumItemModel model = _albumList.get(position);
		binding.itemAlbumTitle.setText(model.getAlbum());
		binding.itemAlbumSubtitle.setText(model.getArtist());
	}

	@Override
	public int getItemCount() {
		return _albumList.size();
	}
}
