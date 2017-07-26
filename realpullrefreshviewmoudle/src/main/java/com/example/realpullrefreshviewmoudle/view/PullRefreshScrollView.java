package com.example.realpullrefreshviewmoudle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.realpullrefreshviewmoudle.R;
import com.example.realpullrefreshviewmoudle.show.ImplScrollViewRefreshStateCall;

import static android.content.ContentValues.TAG;

/**
 * Created by apple on 2017/7/7.
 * 下拉刷新
 */

public class PullRefreshScrollView extends LinearLayout {


    private int mTouchSlop;
    //    分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    //    分别记录上次滑动的坐标(onInterceptTouchEnvent)
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int contentViewId;
    private View contentView;
    private ScrollViewRefreshStateCall mScrollViewRefreshStateCall;



    private NestedScrollView mNestedScrollView;


    private int DEFAULT = 0;
    private final int PULL_DOWN_REFRESH = 1;
    private final int RELEASE_REFRESH = 2;
    private final int REFRESHING = 3;
    private final int LOAD_MORE = 4;
    private int STATE = DEFAULT;


    private int rfreshHeaderWidth;
    private int refreshHeadviewHeight;


    private OnPullListener mOnPullListener;
    private View mRefreshHeaderView;

    private RecyclerView.LayoutManager mLayoutManager;


    int refreshHeadviewId;


    public View getRefreshHeaderView() {
        return mRefreshHeaderView;
    }

    public void setScrollViewRefreshStateCall(ScrollViewRefreshStateCall scrollViewRefreshStateCall) {
        mScrollViewRefreshStateCall = scrollViewRefreshStateCall;
    }


    public void setOnPullListener(OnPullListener onPullListener) {
        mOnPullListener = onPullListener;
    }

    public PullRefreshScrollView(Context context) {
        super(context);

        initView(context);

    }

    public NestedScrollView getNestedScrollView() {
        return mNestedScrollView;
    }

    boolean flag = false;

    public PullRefreshScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);

//      ★★★★★一个坑initAttrs方法里的typedArray去获取属性时，第一次获取的属性全是0，他会马上重走一次构造方法，再次获取一次，才能获得正确的值
//      如果第一次获取的值为0，则不去initView

//      由于用户可能没有设置refreshHeadviewId，也会获得为0，所以不能以refreshHeadviewId=0作为判断
//       在这里以flag作为一个标识，第一遍走initAttrs时，将flag设为true，也就是第二次才走initView
       /* if (refreshHeadviewId != 0) {
            initView(context);
        }*/
        if (flag) {
            initView(context);
        }

    }


    public PullRefreshScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);


        if (flag) {
            initView(context);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullScrollRefreshView);

        try {

            refreshHeadviewId = typedArray.getResourceId(R.styleable.PullScrollRefreshView_scroll_refresh_header_view, 0);
            contentViewId = typedArray.getResourceId(R.styleable.PullScrollRefreshView_content_view, 0);

            flag = true;

        } finally {
            typedArray.recycle();

        }


    }


    private void initView(Context context) {

        setOrientation(VERTICAL);
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();


//        添加headerview

//       ★ ★ ★ ★ ★ 注意不要用这个方法inflate布局，会导致layout的所有属性失效，height、width、margin
//        原因见  http://blog.csdn.net/zhaokaiqiang1992/article/details/36006467
//       ★ ★ ★ ★ ★ mRefreshHeaderView = mInflater.inflate(R.layout.headerview_moren, null);


        if (refreshHeadviewId == 0) {
            mRefreshHeaderView = LayoutInflater.from(context).inflate(R.layout.headerview_moren, this, false);

            //      这里我提供了一个默认的显示效果，如果用户不自己设置的话，会默认使用这个
//           用户可以实现ScrollViewRefreshStateCall接口，去实现自己想要的显示效果，
//          注意要先增加自己的头布局，对应于你自己的头布局操作动画
            mScrollViewRefreshStateCall = new ImplScrollViewRefreshStateCall(this);
        } else {
            mRefreshHeaderView = LayoutInflater.from(context).inflate(refreshHeadviewId, this, false);
            if(mScrollViewRefreshStateCall==null){
                throw new RuntimeException("由于您使用了自定义的头布局，你要使用setScrollViewRefreshStateCall()方法，自定义一个该布局的动画效果，看参照ImplScrollViewRefreshStateCall");
            }
        }

        addView(mRefreshHeaderView);
//        }

        //        以下代码主要是为了设置头布局的marginTop值为-headerviewHeight
//        注意必须等到一小会才会得到正确的头布局宽高
        postDelayed(new Runnable() {
            @Override
            public void run() {
                rfreshHeaderWidth = mRefreshHeaderView.getWidth();
                refreshHeadviewHeight = mRefreshHeaderView.getHeight();

                MarginLayoutParams lp = new LayoutParams(rfreshHeaderWidth, refreshHeadviewHeight);
                lp.setMargins(0, -refreshHeadviewHeight, 0, 0);
                mRefreshHeaderView.setLayoutParams(lp);

            }
        }, 10);


//      添加ScrollerView
        mNestedScrollView = new NestedScrollView(context);

        if (contentViewId != 0) {
            contentView = LayoutInflater.from(context).inflate(contentViewId, mNestedScrollView, false);
            mNestedScrollView.addView(contentView,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        addView(mNestedScrollView, LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);




    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        下面是讨巧的方法，但是可能要渲染两次
//        View thirdView = getChildAt(3);
//        if (thirdView!=null){
//            throw  new RuntimeException("ScrollView内部只能有一个直接子View");
//        }
//
////       得到第三个子View，这个view其实要放在mNestedScrollView内显示
//        View contentView = getChildAt(2);
////       从父布局中先移除这个view
//        ((ViewGroup) contentView.getParent()).removeView(contentView);
////        再将它显示到mNestedScrollView中
//        mNestedScrollView.addView(contentView);


    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                intercepted = false;
                if (STATE != DEFAULT || STATE != REFRESHING) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;


                //               ******************这里说明什么规则下，拦截，其余代码不要动了，其余代码指的是处理滑动冲突的代码***************


                if (mNestedScrollView.getScrollY() == 0 && deltaY > 0 && Math.abs(deltaY) > Math.abs(deltaX)) {//拉倒最顶部，继续往下拉，将拉出头布局，要父布局拦截
                    intercepted = true;
                } else if (getScrollY() < 0 && Math.abs(deltaY) > Math.abs(deltaX)) {//表示头布局已经向下拉出来，头布局已经显示了，要父布局拦截
                    intercepted = true;

                } else if (deltaY < 0) {
                    intercepted = false;//不要父布局拦截了

                } else {
                    intercepted = false;//不要父布局拦截了
                }
                //                ******************什么规则下，拦截***************

                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;

                break;
            }
            default:
                break;
        }

        Log.d(TAG, "intercepted=" + intercepted);
        mLastX = x;
        mLastY = y;
        mLastXIntercept = x;
        mLastYIntercept = y;

        return intercepted;
    }


    /**
     * 下面不同布局，不同的滑动需求
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (getScrollY() > 0) {
                    //防止在正在刷新状态下，上拉出空白
                } else if (getScrollY() <= 0 && getScrollY() > -refreshHeadviewHeight * 5) {
//                    最多下拉到头布局高度5倍的距离
                    scrollBy(0, -deltaY / 2);
                }

                if (getScrollY() > -refreshHeadviewHeight && STATE != REFRESHING) {//头布局显示不全时，为下拉刷新PULL_DOWN_REFRESH状态
                    STATE = PULL_DOWN_REFRESH;

                    if (mScrollViewRefreshStateCall != null) {
                        mScrollViewRefreshStateCall.onPullDownRefreshState(getScrollY(), refreshHeadviewHeight, deltaY);
                    }

                }
                if (getScrollY() < -refreshHeadviewHeight && STATE != REFRESHING) {//头布局完全显示时，为释放刷新RELEASE_REFRESH状态
                    STATE = RELEASE_REFRESH;
                    if (mScrollViewRefreshStateCall != null) {
                        mScrollViewRefreshStateCall.onReleaseRefreshState(getScrollY(), deltaY);
                    }

                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                final int scrollY = getScrollY();
                //松手时，根据所处的状态，让布局滑动到不同的地方，做不同的操作
                switch (STATE) {

                    case PULL_DOWN_REFRESH:
                        STATE = DEFAULT;
                        //头布局没有完全显示，完全隐藏头布局
                        smoothScrollBy(0, -scrollY);
                        break;
                    case RELEASE_REFRESH:
                        STATE = REFRESHING;
                        smoothScrollBy(0, -refreshHeadviewHeight - scrollY);


                        if (mScrollViewRefreshStateCall != null) {
                            mScrollViewRefreshStateCall.onRefreshingState();
                        }


                        if (mOnPullListener != null) {
                            mOnPullListener.onRefresh();
                        }
                        break;
                    case REFRESHING:
                        if (getScrollY() < -refreshHeadviewHeight) {
                            smoothScrollBy(0, -refreshHeadviewHeight - scrollY);
                        } else {
                            smoothScrollBy(0, -scrollY);
                        }
                        break;
                }

                mVelocityTracker.clear();
                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     * 当用户使用完下拉刷新回调时，需要调用此方法，将头不去隐藏，将STATE恢复
     */
    public void refreshFinish() {
        smoothScrollBy(0, 0 - getScrollY());
        STATE = DEFAULT;
        if (mScrollViewRefreshStateCall != null) {
            mScrollViewRefreshStateCall.onDefaultState();
        }

        Toast.makeText(getContext(), "刷新成功!", Toast.LENGTH_SHORT).show();
    }


    /**
     * 在500毫秒内平滑地滚动多少像素点
     *
     * @param dx
     * @param dy
     */
    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(0, getScrollY(), 0, dy, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }


//    ***************


//    *****************

    /**
     * 回调接口
     */
    public interface OnPullListener {
        /**
         * 当下拉刷新正在刷新时，这时候可以去请求数据，记得最后调用refreshFinish()复位
         */
        void onRefresh();


    }


    /**
     * 回调接口，可以通过下面的回调，自定义各种状态下的显示效果
     * 可以根据下拉距离scrollY设计动画效果
     */
    public interface ScrollViewRefreshStateCall {

        /**
         * 当处于下拉刷新时，头布局显示效果
         *
         * @param scrollY        下拉的距离
         * @param headviewHeight 头布局高度
         * @param deltaY         moveY-lastMoveY,正值为向下拉
         */
        void onPullDownRefreshState(int scrollY, int headviewHeight, int deltaY);

        /**
         * 当处于松手刷新时，头布局显示效果
         *
         * @param scrollY 下拉的距离
         * @param deltaY  moveY-lastMoveY,正值为向下拉
         */
        void onReleaseRefreshState(int scrollY, int deltaY);

        /**
         * 正在刷新时，页面的显示效果
         */
        void onRefreshingState();

        /**
         * 默认状态时，页面显示效果，主要是为了复位各种状态
         */
        void onDefaultState();


    }
}
