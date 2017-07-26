package com.example.apple.realpullrefreshview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.realpullrefreshviewmoudle.view.RealPullRefreshScrollView;

public class ScrollActivity extends AppCompatActivity {

    private RealPullRefreshScrollView mRealPullRefreshScrollView;
    private TextView mTv;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        mHandler = new Handler();
        mRealPullRefreshScrollView = (RealPullRefreshScrollView) findViewById(R.id.real_pull_refresh_scroll_view);

        mTv = (TextView) findViewById(R.id.tv111);
        mRealPullRefreshScrollView.setOnPullListener(new RealPullRefreshScrollView.OnPullListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTv.setText("fsjoofjgofosojgosrterp");
                        mRealPullRefreshScrollView.refreshFinish();
                    }
                },3000);

            }


        });

    }
}
