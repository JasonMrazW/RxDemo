package com.jasonmrazw.rxdemo.rx;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by jasonmrazw on 16/7/24.
 */
public class RxBus{
    private static final String TAG = "RxBus";
    private Subject<Object,Object> mSubject;

    private static RxBus mDefaultInstance;

    private static final Object mLock = new Object();

    public RxBus(){
        mSubject = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getDefault(){
            if(mDefaultInstance==null){
                synchronized (mLock){
                    if(mDefaultInstance==null){
                        mDefaultInstance = new RxBus();
                    }
                }
            }
            return mDefaultInstance;
    }

    public void post(Object t){
        mSubject.onNext(t);
    }

    public Observable toObservable(){
        return mSubject.asObservable();
    }

    public void regist(Subscriber<Object> subscriber){
        mSubject.subscribe(subscriber);
    }

    public void registToMainThread(Subscriber<Object> subscriber){
        mSubject.asObservable().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }
}
