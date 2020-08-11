package com.muiz6.musicplayer.ui.main.home.library.artists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowArtistBinding;
import com.muiz6.musicplayer.ui.MyViewHolder;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<MyViewHolder> {

	private final List<ArtistItemModel> _artistList;

	public ArtistAdapter(List<ArtistItemModel> artistList) {
		_artistList = artistList;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.row_artist, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		final RowArtistBinding binding = RowArtistBinding.bind(holder.itemView);
		final ArtistItemModel model = _artistList.get(position);
		binding.rowArtistTitle.setText(model.getArtist());
		binding.rowArtistAlbumCount.setText(String.valueOf(model.getAlbumCount()));
		binding.rowArtistSongCount.setText(String.valueOf(model.getSongCount()));
	}

	@Override
	public int getItemCount() {
		return _artistList.size();
	}
}
