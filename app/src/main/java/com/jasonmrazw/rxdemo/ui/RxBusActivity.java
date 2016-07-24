package com.jasonmrazw.rxdemo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.jasonmrazw.rxdemo.R;
import com.jasonmrazw.rxdemo.event.UpdateUIEvent;
import com.jasonmrazw.rxdemo.rx.MultiClickSubscribe;
import com.jasonmrazw.rxdemo.rx.RxBus;
import com.jasonmrazw.rxdemo.service.MyService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class RxBusActivity extends AppCompatActivity {

    RxBus mBus;

    @BindView(R.id.show_click)
    Button mShowClick;

    Observable<Integer> mClickStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);

        ButterKnife.bind(this);

        mClickStream = Observable.create(new MultiClickSubscribe(mShowClick));

        mClickStream.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer o) {
                mBus.post("click me");
            }
        });

        mBus = new RxBus();

        mBus.regist(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                mShowClick.setText(o.toString());
            }
        });


        RxBus.getDefault().regist(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                mShowClick.setText(o.toString());
            }
        });

        /**
         * start myService
         */
        Intent intent = new Intent(this,MyService.class);
        startService(intent);

    }

}
