package com.example.apple.realpullrefreshview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.realpullrefreshviewmoudle.view.PullRefreshScrollView;

public class ScrollActivity extends AppCompatActivity {

    private PullRefreshScrollView mPullRefreshScrollView;
    private TextView mTv;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        mHandler = new Handler();
        mPullRefreshScrollView = (PullRefreshScrollView) findViewById(R.id.real_pull_refresh_scroll_view);
//        mPullRefreshScrollView.setScrollViewRefreshStateCall(new PullRefreshScrollView.ScrollViewRefreshStateCall() {
//            @Override
//            public void onPullDownRefreshState(int scrollY, int headviewHeight, int deltaY) {
//
//            }
//
//            @Override
//            public void onReleaseRefreshState(int scrollY, int deltaY) {
//
//            }
//
//            @Override
//            public void onRefreshingState() {
//
//            }
//
//            @Override
//            public void onDefaultState() {
//
//            }
//        });
        mTv = (TextView) findViewById(R.id.tv111);
        mPullRefreshScrollView.setOnPullListener(new PullRefreshScrollView.OnPullListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTv.setText("fsjoofjgofosojgosrterp");
                        mPullRefreshScrollView.refreshFinish();
                    }
                }, 3000);

            }


        });

    }
}
