package com.muiz6.musicplayer.ui.main.home.albums;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.ItemAlbumBinding;
import com.muiz6.musicplayer.ui.main.home.MyViewHolder;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<MyViewHolder> {

	private final List<MediaBrowserCompat.MediaItem> _albumList;

	public AlbumAdapter(List<MediaBrowserCompat.MediaItem> albumList) {
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
		final MediaDescriptionCompat mediaDescription = _albumList.get(position).getDescription();
		binding.itemAlbumTitle.setText(mediaDescription.getTitle());
		final CharSequence subtitle = mediaDescription.getSubtitle();
		if (subtitle != null) {
			binding.itemAlbumSubtitle.setText(subtitle.toString());
		}
	}

	@Override
	public int getItemCount() {
		return _albumList.size();
	}
}
