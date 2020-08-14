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

			// store permission in two collections one for pending requests, other for pending
			// callbacks remove permission from pending requests when permission has been requested
			// by the activity, and remove from pending callbacks when activity has responded to
			// the request
			final PermissionRequest request = new PermissionRequest(_setPendingRequests.size(),
					permission,
					callback);
			_setPendingRequests.add(request);
			_setPendingCallbacks.add(request);
			_tryRequestPermission();
		}
		else {
			callback.onRequestPermissionResult(true);
		}
	}

	public void setActivity(Activity activity) {
		_activity = activity;

		// if there are any requests made before activity is created handle them now
		if (Build.VERSION.SDK_INT >= 23 && _activity != null) {
			_tryRequestPermission();
		}
	}

	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults) {
		for (final PermissionRequest request : _setPendingCallbacks) {
			if (requestCode == request.getRequestCode()) {

				// only one permission is requested at a time
				final boolean result = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				request.getCallback().onRequestPermissionResult(result);
				_setPendingCallbacks.remove(request);
			}
		}
	}

	public boolean hasPermission(String permission) {
		if (_activity != null) {
			return ActivityCompat.checkSelfPermission(_activity, permission) ==
					PackageManager.PERMISSION_GRANTED;
		}
		return false;
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void _tryRequestPermission() {
		if (_activity != null) {
			for (final PermissionRequest request : _setPendingRequests) {
				if (ActivityCompat.checkSelfPermission(_activity, request.getPermission()) ==
						PackageManager.PERMISSION_GRANTED) {
					request.getCallback().onRequestPermissionResult(true);
					_setPendingRequests.remove(request);
					_setPendingCallbacks.remove(request);
				}
				else {
					_activity.requestPermissions(new String[]{request.getPermission()},
							request.getRequestCode());
					_setPendingRequests.remove(request);
				}
			}
		}
	}

	public interface Callback {

		/**
		 * Callback to be called when system has responded to permission request. Will not be called
		 * if activity is never created.
		 * @param granted if the permission was granted, false otherwise
		 */
		void onRequestPermissionResult(boolean granted);
	}
}
