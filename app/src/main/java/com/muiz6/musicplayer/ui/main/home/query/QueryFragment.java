package com.muiz6.musicplayer.ui.main.home.query;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.muiz6.musicplayer.databinding.FragmentQueryBinding;

public class QueryFragment extends Fragment {

	private FragmentQueryBinding _binding;

	public QueryFragment() {}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentQueryBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}
}
