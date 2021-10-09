package com.muiz6.musicplayer.ui;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Util {

	private static final SimpleDateFormat _FORMAT = new SimpleDateFormat("mm:ss");

	@NonNull
	public static String millisecondToString(int millis) {
		return _FORMAT.format(new Date(millis));
	}
}
