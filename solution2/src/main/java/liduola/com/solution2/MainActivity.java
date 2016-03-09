package liduola.com.solution2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ViewPager mViewPager;
    private View mTopLayout;
    private RelativeLayout.LayoutParams mTopParams;//TopLayout的layout参数
    private int[] mScrollYs = new int[4];//记录四个RecyclerView的滚动值
    private RecyclerView[] mRecyclerViews = new RecyclerView[4];
    private int mIndex = 0;//表示当前在Viewpager的第几页,即第几个recyclerView在前台展示.
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
        mViewPager.setOffscreenPageLimit(4);//使4个page都一直保留,不会回收.
    }

    /**
     * 让index对应的recyclerView 滚动到Y.
     *
     * @param index 序号
     * @param y     需要滚动到Y
     */
    private void scrollRecyclerViewTo(int index, int y) {
        //因为recyclerView没有实现scrollTo方法,只能用scrollBy达到同样的效果.
        mRecyclerViews[index].scrollBy(0, y - mScrollYs[index]);
//        mScrollYs[index] = y;//更新对应recyclerView的滚动值.
    }

    //ViewPager的适配器,就是产生4个recyclerView作为4个page.
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
            recyclerView.setLayoutManager(staggeredGridLayoutManager);//让recyclerView为双列的瀑布流布局
            recyclerView.setAdapter(mRecyclerAdapter);
            recyclerView.addOnScrollListener(mRecyclerScrollListener);
            recyclerView.setClipToPadding(false);//让recyclerView可以滚动到padding所占的部分.
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

    //RecyclerAdapter的适配器方法,随便写的,不重要.
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
            mScrollYs[index] += dy;//实时更新对应RecyclerView的滚动值.
            if (index != mIndex) return;//如果正在滚动的不是当前在展示的RecyclerView,就无需调整topView.

            //如果RecyclerView滚动值小于500,mTopLayout和滚动值的负值保持一致,否则就是-500.
            // 500这个值可以随便设置成别的值
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
            mIndex = position;//使mIndex的值保持为当前页的序号
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                //让当前页左边那页的recyclerView 滑动到和当前TopView正好衔接的位置
                if (mIndex > 0) {
                    scrollRecyclerViewTo(mIndex - 1, -mTopParams.topMargin);
                }
                //让当前页右边那页的recyclerView 滑动到和当前TopView正好衔接的位置
                if (mIndex < 3) {
                    scrollRecyclerViewTo(mIndex + 1, -mTopParams.topMargin);
                }
            }
        }
    }


}
