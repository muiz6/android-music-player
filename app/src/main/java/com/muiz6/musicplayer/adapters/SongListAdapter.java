package com.muiz6.musicplayer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.models.SongData;

public class SongListAdapter extends ArrayAdapter<SongData> {
    private final Context _CONTEXT;
    private final int _RESOURCE;
    private final ArrayList<SongData> _ITEMS;

    public SongListAdapter(@NonNull Context context, int resource, ArrayList<SongData> items) {
        super(context, resource, items);
        _RESOURCE = resource;
        _CONTEXT = context;
        _ITEMS = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        LayoutInflater inflater = LayoutInflater.from(_CONTEXT);
        convertView = inflater.inflate(_RESOURCE, parent, false);

        TextView songItemWidgetTitle = (TextView) convertView.findViewById(R.id.widget_song_item_title);
        songItemWidgetTitle.setText(_ITEMS.get(position).getTitle());
        songItemWidgetTitle.setTextColor(0xFFFFFFFF);

        TextView songItemWidgetPath = (TextView) convertView.findViewById(R.id.widget_song_item_path);
        songItemWidgetPath.setText(_ITEMS.get(position).getPath());

        return convertView;
    }
}
