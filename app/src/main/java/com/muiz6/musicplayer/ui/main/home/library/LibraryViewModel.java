package com.muiz6.musicplayer.ui.main.home.library;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.data.MusicRepository;

import javax.inject.Inject;

public class LibraryViewModel extends ViewModel {

	private final MusicRepository _repository;

	@Inject
	public LibraryViewModel(MusicRepository repository) {
		_repository = repository;
	}

	public void onRescanLibraryAction() {
		_repository.scanMusicLibrary();
	}
}
