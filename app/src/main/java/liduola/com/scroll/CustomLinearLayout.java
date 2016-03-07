package liduola.com.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 兼容滚动的控件,其中child1与child2组成所有内容.
 */
public class CustomLinearLayout extends LinearLayout {
    public static final String TAG = "CustomLinearLayout";

    private int mLastRawY; //手指上一次的Y位置
    private int mLastRawX; //手指上一次的X位置
    private int mCurrentRawY; //本次手指的位置
//    private int mDeltaY; //表示通过内容滚动,从而往下显示的距离

    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private Context mContext;
    private Child2 mChild2;
    private LayoutParams mChild1Params; //

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        initData();

    }

    private void init(Context context) {
        mContext = context;
        mScroller = new Scroller(mContext);

    }

    private void initData() {
        mChild1Params = (LayoutParams) getChildAt(0).getLayoutParams();
    }

    public void setChild2(Child2 child2) {
        mChild2 = child2;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mScroller.forceFinished(true);
                intercepted = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastRawX;
                int deltaY = y - mLastRawY;
                intercepted = Math.abs(deltaX) < Math.abs(deltaY);
                break;
            }

            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
            default:
                break;

        }
        mLastRawX = x;
        mLastRawY = y;
        return intercepted;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "TouchEvent action down");
                mLastRawY = (int) event.getRawY();
                mScroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v(TAG, "TouchEvent action move");
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                    Log.d(TAG, "tracker obtain");
                }
                mVelocityTracker.addMovement(event);
                mCurrentRawY = (int) event.getRawY();
                int deltaY = -(mCurrentRawY - mLastRawY);//当手指往下滑时,内容需要往上显示.
                mLastRawY = mCurrentRawY;
                int destY = mChild2.getScrollY() - mChild1Params.topMargin + deltaY;
                scrollTo(destY);
                Log.v(TAG, "destY:" + destY);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "TouchEvent action up");

                mLastRawY = 0;
                mCurrentRawY = 0;
                Log.d(TAG, "tracker use");
                if (mVelocityTracker != null) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    int yVelocity = (int) mVelocityTracker.getYVelocity();
                    Log.d(TAG, "yVelocity : " + yVelocity);
                    mScroller.forceFinished(true);
                    mScroller.fling(0, mChild2.getScrollY() - mChild1Params.topMargin, 0,
                            -yVelocity, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    Log.d(TAG, "tracker clear");
                }

                break;
            default:

        }
        invalidate();
        return true;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrY());
            Log.v(TAG, "mScroller.getCurrY():" + mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void scrollTo(int y) {
        if (y > mChild2.getScrollY() - mChild1Params.topMargin) { //如果内容要向下显示
            if (-mChild1Params.topMargin < mChild1Params.height) { //如果child1没有完全隐藏
                mChild1Params.topMargin = -(y - mChild2.getScrollY());
                //如果child1滑超了,修正为刚好全部隐藏
                mChild1Params.topMargin = -mChild1Params.topMargin <= mChild1Params
                        .height ? mChild1Params.topMargin : -mChild1Params.height;
                requestLayout();
            } else if (-mChild1Params.topMargin == mChild1Params.height) { //child1已经完全滑走
                mChild2.scrollTo(y + mChild1Params.topMargin);
            }
        } else if (y < mChild2.getScrollY() - mChild1Params.topMargin) { //如果内容要向上显示
            if (mChild2.getScrollY() > 0) { //如果child2有滑动过,
                mChild2.scrollTo(y + mChild1Params.topMargin);
            } else if (mChild2.getScrollY() == 0) {//如果child2没有滑动
                y = y >= 0 ? y : 0; // 如果y小于0了,就修正为0
                mChild1Params.topMargin = -y;
                requestLayout();
            }
        }

    }


    public interface Child2 {
        int getScrollY();

        void scrollTo(int y);
    }


}
