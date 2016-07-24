package com.jasonmrazw.rxdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.jasonmrazw.rxdemo.event.UpdateUIEvent;
import com.jasonmrazw.rxdemo.rx.RxBus;

import org.greenrobot.eventbus.EventBus;

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                RxBus.getDefault().post("service event");
            }
        },1000);
    }
}
