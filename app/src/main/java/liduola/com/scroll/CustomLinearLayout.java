package liduola.com.scroll;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * 兼容滚动的控件
 */
public class CustomLinearLayout extends LinearLayout {
    public static final String TAG = "CustomLinearLayout";

    private int mLastY;
    private int mCurrentY;
    private View mChild1;
    private View mChild2;
    private int mChildScrollY = 0;
    private int mPointerId;
    private int mDeltaY;
    VelocityTracker mVelocityTracker;



    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        initData();

    }

    private void initData() {
        mChild1 = getChildAt(0);
        mChild2 = getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
            mVelocityTracker.addMovement(event);

        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) event.getRawY();
                mPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentY = (int) event.getRawY();
                mDeltaY = mCurrentY - mLastY;
                mLastY = mCurrentY;
                scrollBy(mDeltaY);

                break;
            case MotionEvent.ACTION_UP:

                fling(mDeltaY );
                mLastY = 0;
                mCurrentY = 0;

                mVelocityTracker.computeCurrentVelocity(1000);
                int yVelocity = (int) mVelocityTracker.getYVelocity();
                Log.d(TAG, "yVelocity : " + yVelocity);

                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
            default:

        }
        return true;
    }

    /**
     *
     */
    private void scrollBy(int deltaY) {
        LinearLayout.LayoutParams params = (LayoutParams) mChild1.getLayoutParams();
        Log.v(TAG, "topMargin: " + params.topMargin);
        Log.v(TAG, "height: " + params.height);
        Log.v(TAG, "deltaY: " + deltaY);
        Log.v(TAG, "scrollY: " + mChildScrollY);
        if (deltaY < 0) {
            if (params.topMargin < -params.height) {
                mChild2.scrollBy(0, -deltaY);
                mChildScrollY = mChildScrollY - deltaY;
            } else {
                params.topMargin += deltaY;
                Log.v(TAG, "else topMargin: " + params.topMargin);
                requestLayout();
            }
        } else if (deltaY > 0) {
            if (mChildScrollY > 0) {
                mChild2.scrollBy(0, -deltaY);
                mChildScrollY = mChildScrollY - deltaY;
            } else {
                params.topMargin += deltaY;
                requestLayout();
            }
            
        }

    }

    private void fling(final int lastDeltaY) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(Math.abs(lastDeltaY * 5));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float lastFraction = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int deltaY = (int) (-lastDeltaY * Math.abs(lastDeltaY)/10* (fraction -
                        lastFraction));
                lastFraction = fraction;
                scrollBy(-deltaY);
                Log.v(TAG, "fraction: " + fraction);


            }
        });
        animator.start();
    }


}
