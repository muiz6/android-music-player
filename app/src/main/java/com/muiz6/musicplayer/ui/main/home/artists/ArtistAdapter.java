package com.muiz6.musicplayer.ui.main.home.artists;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowArtistBinding;
import com.muiz6.musicplayer.ui.main.home.MyViewHolder;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<MyViewHolder> {

	private final List<MediaBrowserCompat.MediaItem> _artistList;

	public ArtistAdapter(List<MediaBrowserCompat.MediaItem> artistList) {
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
		final MediaDescriptionCompat mediaDescription = _artistList.get(position).getDescription();
		binding.rowArtistTitle.setText(mediaDescription.getTitle());
		final CharSequence subtitle = mediaDescription.getSubtitle();
		if (subtitle != null) {
			binding.rowArtistSubtitle.setText(subtitle.toString());
		}
	}

	@Override
	public int getItemCount() {
		return _artistList.size();
	}
}
