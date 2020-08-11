package com.muiz6.musicplayer.ui.main.home.library.albums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.ItemAlbumBinding;
import com.muiz6.musicplayer.ui.MyViewHolder;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<MyViewHolder> {

	private final List<AlbumItemModel> _albumList;

	public AlbumAdapter(List<AlbumItemModel> albumList) {
		_albumList = albumList;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_album, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		final ItemAlbumBinding binding = ItemAlbumBinding.bind(holder.itemView);
		final AlbumItemModel model = _albumList.get(position);
		binding.itemAlbumTitle.setText(model.getAlbum());
		binding.itemAlbumSubtitle.setText(model.getArtist());
	}

	@Override
	public int getItemCount() {
		return _albumList.size();
	}
}
