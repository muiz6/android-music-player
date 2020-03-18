package com.muiz6.musicplayer.viewmodels;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.services.AudioService;

public class MainActivityViewModel extends ViewModel {

    private final ServiceConnection mServiceConnection;
    private MutableLiveData<AudioService.MyBinder> mBinder;

    public MainActivityViewModel() {
        mServiceConnection  = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AudioService.MyBinder binder = (AudioService.MyBinder) service;
                mBinder.postValue(binder);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBinder.postValue(null);
            }
        };
    }

    public LiveData<AudioService.MyBinder> getBinder() {
        return mBinder;
    }

    public ServiceConnection getServiceConnection() {
        return mServiceConnection;
    }
}
