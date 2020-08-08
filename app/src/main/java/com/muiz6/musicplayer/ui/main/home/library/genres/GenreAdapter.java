package com.muiz6.musicplayer.ui.main.home.library.genres;

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

	private final List<GenreItemModel> _genreList;

	public GenreAdapter(List<GenreItemModel> genreList) {
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
		final GenreItemModel model = _genreList.get(position);
		binding.rowGenreTitle.setText(model.getGenre());
		binding.rowGenreSubtitle.setText(model.getArtist());
		binding.rowGenreSongCount.setText(String.valueOf(model.getSongCount()));
	}

	@Override
	public int getItemCount() {
		return _genreList.size();
	}
}
