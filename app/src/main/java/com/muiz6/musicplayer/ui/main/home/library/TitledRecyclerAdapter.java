package com.muiz6.musicplayer.ui.main.home.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.recyclerviewutil.DefaultRecyclerAdapter;
import com.muiz6.musicplayer.ui.recyclerviewutil.DefaultViewHolder;

public abstract class TitledRecyclerAdapter<T extends RecyclerView.ViewHolder>
		extends DefaultRecyclerAdapter<T> {

	@NonNull
	@Override
	public T onCreateViewHolder(@NonNull LayoutInflater inflater,
			@NonNull ViewGroup parent,
			int viewType) {
		if (viewType == R.layout.row_list_header) {
			final View view = inflater.inflate(R.layout.row_list_header, parent, false);

			// todo: fix this
			return (T) new DefaultViewHolder(view);
		}
		throw new IllegalStateException("View type is not 'R.layout.row_list_header'");
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return R.layout.row_list_header;
		}
		return super.getItemViewType(position);
	}
}
