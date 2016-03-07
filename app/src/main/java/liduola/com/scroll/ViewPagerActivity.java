package liduola.com.scroll;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewPagerActivity extends AppCompatActivity {

    private CustomLinearLayout mCustomLinearLayout;
    private ViewPager mViewPager;
    private int[] mScrollYs = new int[4];
    private int mCurrentPosition;
    private RecyclerView[] mRecyclerViews = new RecyclerView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        init();
        initViewpager();
        initLinearLayout();
    }

    private void init() {
        mCustomLinearLayout = (CustomLinearLayout) findViewById(R.id.customLinearLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void initViewpager() {
        mViewPager.setAdapter(new Adapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(4);
    }

    private void initLinearLayout() {
        mCustomLinearLayout.setChild2(new CustomLinearLayout.Child2() {
            @Override
            public int getScrollY() {
                return mScrollYs[mCurrentPosition];
            }

            @Override
            public void scrollTo(int y) {
                mRecyclerViews[mCurrentPosition].scrollBy(0, y - mScrollYs[mCurrentPosition]);
            }
        });
    }

    private class Adapter extends PagerAdapter {
        private RecyclerView.Adapter mRecyclerAdapter;
        private RecyclerView.OnScrollListener mScrollListener;

        public Adapter() {
            mRecyclerAdapter = new RecyclerView.Adapter() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int
                        viewType) {
                    TextView textView = new TextView(ViewPagerActivity.this);

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
            };
            mScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    mScrollYs[mCurrentPosition] += dy;
                }
            };

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RecyclerView recyclerView = new RecyclerView(ViewPagerActivity.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new
                    StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
            recyclerView.setAdapter(mRecyclerAdapter);
            recyclerView.addOnScrollListener(mScrollListener);
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

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mRecyclerViews[position]);
            Log.d("ViewPagerActivity", "destroyItem");
        }
    }
}
