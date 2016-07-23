package com.jasonmrazw.rxdemo.rx;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by jasonmrazw on 16/7/23.
 */
public class MultiClickSubscribe implements Observable.OnSubscribe<Integer>, View.OnClickListener {

    private View mView;

    private List<Subscriber<? super Integer>> mSubscribers = new ArrayList<>();
    private int number = 0;

    public MultiClickSubscribe(View view) {
        mView = view;
    }

    @Override
    public void call(Subscriber<? super Integer> subscriber) {
        mSubscribers.add(subscriber);

        mView.setOnClickListener(this);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                mView.setOnClickListener(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        for(Subscriber subscriber :mSubscribers){
            subscriber.onNext(number++);
        }
    }
}
