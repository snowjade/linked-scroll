package liduola.com.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 兼容滚动的控件,其中child1与child2组成所有内容.
 */
public class CustomLinearLayout extends LinearLayout {
    public static final String TAG = "CustomLinearLayout";

    private int mLastRawY; //手指上一次的位置
    private int mCurrentRawY; //本次手指的位置
    private View mChild1;
    private int mScrollY = 0; //内容滑动的距离
    private int mDeltaY; //表示通过内容滚动,从而往下显示的距离
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private Context mContext;
    private Child2 mChild2;

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
    }
    private void initData() {
        mChild1 = getChildAt(0);
        mScroller = new Scroller(mContext);
    }

    public void setChild2(Child2 child2) {
        mChild2 = child2;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);
                mLastRawY = (int) event.getRawY();
                mScroller.forceFinished(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);
                mCurrentRawY = (int) event.getRawY();
                mDeltaY = -(mCurrentRawY - mLastRawY);//当手指往下滑时,内容需要往上显示.
                mLastRawY = mCurrentRawY;
                scrollBy(mDeltaY);
                Log.d(TAG, "deltaY:" + mDeltaY);
                break;
            case MotionEvent.ACTION_UP:

                mLastRawY = 0;
                mCurrentRawY = 0;
                mVelocityTracker.computeCurrentVelocity(1000);
                int yVelocity = (int) mVelocityTracker.getYVelocity();
                Log.d(TAG, "yVelocity : " + yVelocity);
                mScroller.forceFinished(true);
                mScroller.fling(0, mScrollY, 0, -yVelocity, 0,
                        0, -Integer.MAX_VALUE, Integer.MAX_VALUE);

                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
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
            Log.d(TAG, "mScroller.getCurrY():" + mScroller.getCurrY());
            postInvalidate();
        }
    }


    private void scrollTo(int destY) {
        scrollBy(destY - mScrollY);
    }



    /**
     *
     */
    private void scrollBy(int deltaY) {
        LinearLayout.LayoutParams params = (LayoutParams) mChild1.getLayoutParams();
        Log.v(TAG, "topMargin: " + params.topMargin);
        Log.v(TAG, "height: " + params.height);
        Log.v(TAG, "deltaY: " + deltaY);
        Log.v(TAG, "scrollY: " + mScrollY);
        if (deltaY > 0) { // 内容往下显示
            if (params.topMargin <= -params.height) { //如果child1 完全隐藏了,滑动child2.
                mChild2.scrollBy(deltaY);
                Log.d(TAG, "mChild2.getScrollY: " + mChild2.getScrollY());
                mScrollY = mChild2.getScrollY() + params.height;
                // TODO: 1/26/16
            } else if(params.topMargin > -params.height ) { //如果child1 没有完全显示,移动child1
                params.topMargin -= deltaY;
                mScrollY += deltaY;
                if(params.topMargin < -params.height){ //如果滑动之后会 child1 会移动过多,进行修正.
                    params.topMargin = -params.height;
                    mScrollY = params.height;
                }
                Log.v(TAG, "else topMargin: " + params.topMargin);
                requestLayout();
            }
        } else if (deltaY < 0) { // 内容往上显示
            if (mChild2.getScrollY() > 0) {  // 如果 child2 没有滑到顶,就滑动child2.
                mChild2.scrollBy(deltaY);
                mScrollY += deltaY;
                if(mScrollY - params.height < 0){ //如果 child2 的滑动记录值小于0了,修正为0.
                    int reviseY = mScrollY - params.height;
                    mScrollY = params.height;
                    scrollBy(reviseY);
                }
            } else if( params.topMargin <= 0) {//如果child2 已经滑到顶,并且child1 没到顶,移动child1
                Log.d(TAG, "topMargin: " + params.topMargin);
                params.topMargin -= deltaY;
                mScrollY += deltaY;
                if (params.topMargin > 0) { //如果child1过多的下移,与顶部产生间距了,修正topMargin为0.
                    params.topMargin = 0;
                    mScrollY = 0;
                }
                requestLayout();
            }

        }



    }

    public interface Child2{
        int getScrollY();
        void scrollBy(int deltaY);
    }




}
