package com.muiz6.musicplayer.ui;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Util {

	@NonNull
	public static String millisecondToString(int millis) {
		final SimpleDateFormat format = new SimpleDateFormat("mm:ss");
		return format.format(new Date(millis));
	}
}
