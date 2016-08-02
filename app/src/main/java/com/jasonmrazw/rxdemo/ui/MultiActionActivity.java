package com.jasonmrazw.rxdemo.ui;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jasonmrazw.rxdemo.R;
import com.jasonmrazw.rxdemo.rx.MultiClickSubscribe;
import com.jasonmrazw.rxdemo.rx.RxBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

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

    @BindView(R.id.show_norx_click)
    Button mShowNoRxClick;


    /**
     * click stream
     */
    Observable<Integer> mClickStream;

    Subscription mClickSubscription;

    RxBus mRxBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_action);

        ButterKnife.bind(this);

        /**
         * count for click events
         */
        initRxEvents();

        initNoRxEvents();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mClickSubscription.isUnsubscribed()) {
            mClickSubscription.unsubscribe();
        }
    }

    /**
     * init rx events
     */
    private void initRxEvents() {
        mClickStream = Observable.create(new MultiClickSubscribe(mShowClick));

        mClickSubscription = mClickStream
                .buffer(mClickStream.debounce(600, TimeUnit.MILLISECONDS))
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
                        mShowClick.setText(integer + " click");
                    }
                });

        mClickStream.buffer(mClickStream.debounce(600,TimeUnit.MILLISECONDS))
                .map(new Func1<List<Integer>, Integer>() {
                    @Override
                    public Integer call(List<Integer> integers) {
                        return integers.size();
                    }
                })
                .subscribe(new Action1<Integer>(){
                    @Override
                    public void call(Integer s) {
                        Log.d(TAG,s+"click");
                    }
                });
    }


    private long mLastClickTime;

    private int mClickCount;

    private static final long TIME_SPACE = 600;

    MyHandler mHandler;


    private void initNoRxEvents() {
        mHandler = new MyHandler(mShowNoRxClick);

        mShowNoRxClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long curTime = System.currentTimeMillis();
                if (mLastClickTime != 0 && curTime - mLastClickTime > TIME_SPACE) {
                    mClickCount = 0;
                }

                mClickCount++;
                mLastClickTime = curTime;

                mHandler.removeMessages(MyHandler.WHAT_UPDATEUI);

                Message message = Message.obtain(mHandler,MyHandler.WHAT_UPDATEUI);
                Bundle bundle = new Bundle();
                bundle.putInt(MyHandler.KEY_NUMBER,mClickCount);
                message.setData(bundle);
                mHandler.sendMessageDelayed(message, TIME_SPACE);
            }
        });
    }


    static class MyHandler extends Handler {

        public static final int WHAT_UPDATEUI = 0x001;
        public static final java.lang.String KEY_NUMBER = "number";

        private Button mButton;

        public MyHandler(Button mButton) {
            this.mButton = mButton;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Bundle bundle = msg.getData();
            if(what == WHAT_UPDATEUI){
                mButton.setText(bundle.getInt(KEY_NUMBER)+"click");
            }
        }
    }
}
