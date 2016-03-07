package liduola.com.solution2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ViewPager mViewPager;
    private View mTopLayout;
    private int[] mScrollYs = new int[4];
    private RecyclerView[] mRecyclerViews = new RecyclerView[4];
    private int mIndex = 0;
    private RelativeLayout.LayoutParams mTopParams;
    private RecyclerScrollListener mRecyclerScrollListener = new RecyclerScrollListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTopLayout = findViewById(R.id.topLayout);
        mTopParams = (RelativeLayout.LayoutParams) mTopLayout.getLayoutParams();
        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.addOnPageChangeListener(new OnPageChangerListener());
        mViewPager.setOffscreenPageLimit(4);
    }

    private void scrollRecyclerViewTo(int index, int y) {
        mRecyclerViews[index].scrollBy(0, y - mScrollYs[index]);
        mScrollYs[index] = y;
    }


    private class ViewPagerAdapter extends PagerAdapter {
        private RecyclerView.Adapter mRecyclerAdapter;

        public ViewPagerAdapter() {
            mRecyclerAdapter = new RecyclerAdapter();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RecyclerView recyclerView = new RecyclerView(MainActivity.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setAdapter(mRecyclerAdapter);
            recyclerView.addOnScrollListener(mRecyclerScrollListener);
            recyclerView.setClipToPadding(false);
            recyclerView.setPadding(0, 600, 0, 0);
            recyclerView.setTag(position);
            mRecyclerViews[position] = recyclerView;
            container.addView(recyclerView, params);
            return recyclerView;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private class RecyclerAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int
                viewType) {
            TextView textView = new TextView(MainActivity.this);

            return new ViewHolders(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String s = "this is  test text";
            while (position > 0) {
                s = s + "this is  test text";
                position--;
            }
            ((ViewHolders) holder).textView.setText(s);
        }

        @Override
        public int getItemCount() {
            return 200;
        }

        class ViewHolders extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolders(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
                textView.setPadding(20, 20, 20, 20);
            }
        }
    }

    private class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int index = (int) recyclerView.getTag();
            if (index != mIndex) return;
            mScrollYs[index] += dy;
            Log.d("mScrollYs", index + ":" + mScrollYs[index]);
            if (mScrollYs[index] <= 500) {
                mTopParams.topMargin = -mScrollYs[index];
                mTopLayout.requestLayout();
            } else if (mTopParams.topMargin != -500) {
                mTopParams.topMargin = -500;
                mTopLayout.requestLayout();
            }
        }
    }

    private class OnPageChangerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                if (mIndex > 0) {
                    scrollRecyclerViewTo(mIndex - 1, -mTopParams.topMargin);
                }
                if (mIndex < 3) {
                    scrollRecyclerViewTo(mIndex + 1, -mTopParams.topMargin);
                }
            }
        }
    }


}
