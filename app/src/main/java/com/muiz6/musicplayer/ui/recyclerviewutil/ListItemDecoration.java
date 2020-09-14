package com.muiz6.musicplayer.ui.recyclerviewutil;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemDecoration extends RecyclerView.ItemDecoration {
	private final int _dimHGap, _dimVGap;
	private Integer _hGap, _vGap;

	public ListItemDecoration(@DimenRes int dimGap) {
		_dimHGap = dimGap;
		_dimVGap = dimGap;
	}

	public ListItemDecoration(@DimenRes int dimHGap, @DimenRes int dimVGap) {
		_dimHGap = dimHGap;
		_dimVGap = dimVGap;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect,
			@NonNull View view,
			@NonNull RecyclerView parent,
			@NonNull RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		final Context context = view.getContext();
		if (_hGap == null || _vGap == null) {

			// lazy instantiate once only
			_hGap = Math.round(context.getResources().getDimension(_dimHGap));
			_vGap = Math.round(context.getResources().getDimension(_dimVGap));
		}
		outRect.left = _hGap;
		outRect.right = _hGap;
		outRect.bottom = _vGap;
		final int position = parent.getChildLayoutPosition(view);
		if (position == 0) {
			outRect.top = _vGap;
		}
	}
}
