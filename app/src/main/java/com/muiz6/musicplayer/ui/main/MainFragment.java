package com.muiz6.musicplayer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {
    private LinearLayout view;

    public MainFragment() {
//        required public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.view = new LinearLayout(context);
        this.view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        this.view.setGravity(Gravity.CENTER);
        TextView txt = new TextView(context);
        txt.setText("Text");
        txt.setTextColor(0xFFFFFFFF);
        this.view.addView(txt);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        return this.view;
    }
}