package com.muiz6.musicplayer.ui.recyclerviewutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DefaultRecyclerAdapter<T extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<T> {

	private Context _context;
	private LayoutInflater _inflater;

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		_context = recyclerView.getContext();
		_inflater = LayoutInflater.from(_context);
	}

	@NonNull
	@Override
	public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return onCreateViewHolder(_inflater, parent, viewType);
	}

	@Override
	public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onDetachedFromRecyclerView(recyclerView);

		// avoid memory leaks
		_context = null;
		_inflater = null;
	}

	@NonNull
	public Context requireContext() {
		if (_context == null) {
			throw new IllegalStateException("Recycler View Adapter "
					+ this + " not attached to a context.");
		}
		return _context;
	}

	@NonNull
	public abstract T onCreateViewHolder(@NonNull LayoutInflater inflater,
			@NonNull ViewGroup parent,
			int viewType);
}
