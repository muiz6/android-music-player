package com.muiz6.musicplayer.ui.main.home.query;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.muiz6.musicplayer.databinding.FragmentQueryBinding;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.QueryViewModel;

@FragmentScope
public class QueryFragment extends Fragment {

	private QueryViewModel _queryViewModel;
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
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		_queryViewModel = new ViewModelProvider(requireActivity()).get(QueryViewModel.class);
		_queryViewModel.getSearchUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {

			@Override
			public void onChanged(Uri uri) {
				final String type = uri.getQueryParameter(QueryViewModel.KEY_TYPE);
				if (type != null) {
					if (type.equals(QueryViewModel.TYPE_QUERY)) {
						final String value = uri.getQueryParameter(QueryViewModel.KEY_VALUE);
						if (value != null && !value.isEmpty()) {
							_binding.queryTextView.setText(value);
						}
					}
					else if (type.equals(QueryViewModel.TYPE_ARTIST)) {
						final String value = uri.getQueryParameter(QueryViewModel.KEY_VALUE);
						if (value != null && !value.isEmpty()) {
							_binding.queryTextView.setText(value);
						}
					}
					else if (type.equals(QueryViewModel.TYPE_GENRE)) {
						final String value = uri.getQueryParameter(QueryViewModel.KEY_VALUE);
						if (value != null && !value.isEmpty()) {
							_binding.queryTextView.setText(value);
						}
					}
				}
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		_binding = null;
	}
}
