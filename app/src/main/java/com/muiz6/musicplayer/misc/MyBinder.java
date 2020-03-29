package com.muiz6.musicplayer.misc;

import android.app.Service;
import android.os.Binder;

public class MyBinder extends Binder {

    private Service _service;

    public MyBinder(Service service) {
        _service = service;
    }

    public Service getService() {
        return _service;
    }
}
