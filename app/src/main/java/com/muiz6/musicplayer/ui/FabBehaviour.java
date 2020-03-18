package com.muiz6.musicplayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

public class FabBehaviour extends CoordinatorLayout.Behavior<View> {

    private float max = 0;

    // necessary constructors
    public FabBehaviour() {}

    public FabBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof  ViewPager;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final View child, final View dependency) {
        if (dependency != null) {
            float translation = dependency.getY();
            if (translation > max) max = translation;

            if (translation < max) {
                child.setVisibility(View.INVISIBLE);
            }
            else {
                child.setVisibility(View.VISIBLE);
            }

            child.requestLayout();

            return true;
        }

        // else
        return false;
    }
}
