package com.muiz6.musicplayer.ui.recyclerviewutil;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

	private final int _dimGap;
	private Integer _halfGap;

	public GridItemDecoration(@DimenRes int dimGap) {
		_dimGap = dimGap;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect,
			@NonNull View view,
			@NonNull RecyclerView parent,
			@NonNull RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (_halfGap == null) {
			// lazy instantiate once only
			_halfGap = Math.round(parent.getContext().getResources().getDimension(_dimGap) / 2);

			// called once only
			parent.setPadding(_halfGap, _halfGap, _halfGap, _halfGap);
			parent.setClipToPadding(false);
		}
		outRect.set(_halfGap, _halfGap, _halfGap, _halfGap);
	}
}
