package com.muiz6.musicplayer.permission;

import androidx.annotation.NonNull;

public class PermissionRequest {

	private final int _requestCode;
	private final String _permissions;
	private final PermissionManager.Callback _callback;

	public PermissionRequest(int requestCode,
			@NonNull String permission,
			@NonNull PermissionManager.Callback callback) {
		_requestCode = requestCode;
		_permissions = permission;
		_callback = callback;
	}

	public int getRequestCode() {
		return _requestCode;
	}

	@NonNull
	public String getPermission() {
		return _permissions;
	}

	@NonNull
	public PermissionManager.Callback getCallback() {
		return _callback;
	}
}
