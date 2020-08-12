package com.muiz6.musicplayer.ui.main;

import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentFactory;

import com.muiz6.musicplayer.MyApp;
import com.muiz6.musicplayer.databinding.ActivityMainBinding;
import com.muiz6.musicplayer.permission.PermissionManager;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

	@Inject FragmentFactory factory;
	@Inject PermissionManager permissionManager;
	private ActivityMainBinding _binding;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		((MyApp) getApplication()).getAppComponent().getMainComponent().create(this).inject(this);
		getSupportFragmentManager().setFragmentFactory(factory);
		permissionManager.setActivity(this);
		super.onCreate(savedInstanceState);

		_binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(_binding.getRoot());
	}

	@Override
	protected void onResume() {
		super.onResume();

		// set default volume button action as media volume increase/decrease
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
