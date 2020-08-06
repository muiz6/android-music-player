package com.muiz6.musicplayer.ui.main.home.explore.genres;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowGenreBinding;
import com.muiz6.musicplayer.ui.MyViewHolder;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<MyViewHolder> {

	private final List<MediaBrowserCompat.MediaItem> _genreList;

	public GenreAdapter(List<MediaBrowserCompat.MediaItem> genreList) {
		_genreList = genreList;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.row_genre, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		final RowGenreBinding binding = RowGenreBinding.bind(holder.itemView);
		final MediaDescriptionCompat mediaDescription = _genreList.get(position).getDescription();
		binding.rowGenreTitle.setText(mediaDescription.getTitle());
		binding.rowGenreSubtitle.setText(mediaDescription.getSubtitle());
	}

	@Override
	public int getItemCount() {
		return _genreList.size();
	}
}
