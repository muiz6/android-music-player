package com.muiz6.musicplayer.ui.main.home.library.genres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.RowGenreBinding;
import com.muiz6.musicplayer.databinding.RowListHeaderBinding;
import com.muiz6.musicplayer.ui.main.home.library.TitledRecyclerAdapter;
import com.muiz6.musicplayer.ui.recyclerviewutil.DefaultViewHolder;

import java.util.List;

public class GenreAdapter extends TitledRecyclerAdapter<DefaultViewHolder> {

	private final List<GenreItemModel> _genreList;

	public GenreAdapter(List<GenreItemModel> genreList) {
		_genreList = genreList;
	}

	@NonNull
	@Override
	public DefaultViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
			@NonNull ViewGroup parent,
			int viewType) {
		if (viewType != R.layout.row_list_header) {
			final View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.row_genre, parent, false);
			return new DefaultViewHolder(view);
		}
		return super.onCreateViewHolder(inflater, parent, viewType);
	}

	@Override
	public void onBindViewHolder(@NonNull DefaultViewHolder holder, int position) {
		final int viewType = getItemViewType(position);
		if (viewType == R.layout.row_list_header) {
			final RowListHeaderBinding binding = RowListHeaderBinding.bind(holder.itemView);
			binding.getRoot().setText(R.string.all_genres);
		}
		else {
			final RowGenreBinding binding = RowGenreBinding.bind(holder.itemView);
			final GenreItemModel model = _genreList.get(position);
			binding.rowGenreTitle.setText(model.getGenre());
			binding.rowGenreSubtitle.setText(model.getArtist());
			if (model.getSongCount() != GenreItemModel.UNDEFINED) {
				binding.rowGenreSongCount.setText(String.valueOf(model.getSongCount()));
			}
		}
	}

	@Override
	public int getItemCount() {

		// plus one for title
		return _genreList.size() + 1;
	}
}
