package com.muiz6.musicplayer.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PermissionManager {

	private final Set<PermissionRequest> _setPendingRequests = new HashSet<>();
	private final Set<PermissionRequest> _setPendingCallbacks = new HashSet<>();
	private Activity _activity;

	@Inject
	public PermissionManager() {}

	public void requestPermissionWhenReady(@NonNull String permission, Callback callback) {
		if (Build.VERSION.SDK_INT >= 23) {

			//
			final PermissionRequest request = new PermissionRequest(_setPendingRequests.size(), permission, callback);
			_setPendingRequests.add(request);
			_setPendingCallbacks.add(request);
			_tryRequestPermission();
		}
	}

	public void setActivity(Activity activity) {
		_activity = activity;
		if (Build.VERSION.SDK_INT >= 23 && _activity != null) {
			_tryRequestPermission();
		}
	}

	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults) {
		for (final PermissionRequest request : _setPendingCallbacks) {
			if (requestCode == request.getRequestCode()) {
				final boolean result = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				request.getCallback().onRequestPermissionResult(result);
				_setPendingCallbacks.remove(request);
			}
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void _tryRequestPermission() {
		if (_activity != null) {
			for (final PermissionRequest request : _setPendingRequests) {
				if (ActivityCompat.checkSelfPermission(_activity, request.getPermission()) == PackageManager.PERMISSION_GRANTED) {
					request.getCallback().onRequestPermissionResult(true);
					_setPendingRequests.remove(request);
					_setPendingCallbacks.remove(request);
				}
				else {
					_activity.requestPermissions(new String[]{request.getPermission()}, request.getRequestCode());
					_setPendingRequests.remove(request);
				}
			}
		}
	}

	public interface Callback {
		void onRequestPermissionResult(boolean granted);
	}
}
