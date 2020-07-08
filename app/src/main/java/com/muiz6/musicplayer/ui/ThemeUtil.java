package com.muiz6.musicplayer.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public abstract class ThemeUtil {

	@ColorInt
	public static int getColor(@NonNull Context context, @AttrRes int resId) {
		final Resources.Theme theme = context.getTheme();
		final TypedValue typedValue = new TypedValue();
		theme.resolveAttribute(resId, typedValue, true);
		@ColorRes final int colorId = typedValue.resourceId;
		return ContextCompat.getColor(context, colorId);
	}
}
