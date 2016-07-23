package com.jasonmrazw.rxdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;
import com.jasonmrazw.rxdemo.R;
import com.jasonmrazw.rxdemo.rx.MultiClickSubscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jasonmrazw on 16/7/23.
 * 监听n次点击事件.
 * 原理:
 * c---c----c---c---c----->
 * buffer(debounce)
 * ----cc-------cc------c->
 * map
 * ----2--------2-------1->
 */
public class MultiActionActivity extends AppCompatActivity {

    private static final String TAG = "Multi";
    @BindView(R.id.show_click)
    Button mShowClick;

    /**
     * click stream
     */
    Observable<Integer> mClickStream;

    Subscription mClickSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_action);

        ButterKnife.bind(this);

        /**
         * count for click events
         */
        mClickStream = Observable.create(new MultiClickSubscribe(mShowClick));

        mClickSubscription = mClickStream
                .buffer(mClickStream.debounce(600,TimeUnit.MILLISECONDS))
                .map(new Func1<List<Integer>, Integer>() {
                    @Override
                    public Integer call(List<Integer> integers) {
                        return integers.size();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                      mShowClick.setText(integer+" click");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!mClickSubscription.isUnsubscribed()){
            mClickSubscription.unsubscribe();
        }
    }
}