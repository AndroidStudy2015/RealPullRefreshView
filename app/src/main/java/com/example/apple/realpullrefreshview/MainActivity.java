package com.example.apple.realpullrefreshview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.example.apple.realpullrefreshview.adapter.MyAdapter;
import com.example.apple.realpullrefreshview.bean.Body;
import com.example.realpullrefreshviewmoudle.view.RealPullRefreshView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RealPullRefreshView mRealPullRefreshView;

    private ArrayList<Body> mBodies;

    private LinearLayoutManager mLayoutManager;
    private MyAdapter mMyAdapter;

    Handler mHandler = new Handler();

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mRealPullRefreshView = (RealPullRefreshView) findViewById(R.id.real_pull_refresh_view);


        mLayoutManager = new LinearLayoutManager(this);
        mMyAdapter = new MyAdapter(this, mBodies);
        mRealPullRefreshView.setLayoutManager(mLayoutManager);
        mRealPullRefreshView.setAdapter(mMyAdapter);

        mRealPullRefreshView.setOnPullListener(new RealPullRefreshView.OnPullListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBodies.add(0, new Body("新数据" + i++, 100));
                        mRealPullRefreshView.refreshFinish();
                    }
                }, 3000);
            }

            @Override
            public void onLoadMore() {
                final List<Body>   mMore = new ArrayList<>();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 3; i++) {
                            mMore.add(new Body("more+++" + i, 100));

                        }


                        mBodies.addAll(mMore);
                        mRealPullRefreshView.loadMroeFinish();
                    }
                }, 1500);
            }


        });
    }

    private void initData() {
        mBodies = new ArrayList<>();
        for (int j = 0; j < 17; j++) {
            mBodies.add(new Body("水电费" + j * 5, 100));
        }


    }
}