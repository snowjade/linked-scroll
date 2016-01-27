package liduola.com.scroll;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private CustomLinearLayout mCustomLinearLayout;
    private RelativeLayout mUpPart;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private Activity mActivity;
    private int mScrollY=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mCustomLinearLayout = (CustomLinearLayout) findViewById(R.id.customLinearLayout);
        mCustomLinearLayout.setChild2(new CustomLinearLayout.Child2() {
            @Override
            public int getScrollY() {
                return mScrollY;
            }

            @Override
            public void scrollTo(int y) {
                mRecyclerView.scrollBy(0,y - mScrollY);
            }
        });
        mUpPart = (RelativeLayout) findViewById(R.id.upPart);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textView = new TextView(mActivity);

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
                return 20;
            }

            class ViewHolders extends RecyclerView.ViewHolder {
                TextView textView;

                public ViewHolders(View itemView) {
                    super(itemView);
                    textView = (TextView) itemView;
                    textView.setPadding(20, 20, 20, 20);
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollY +=dy;
                Log.v(TAG, "mScrollY: " + mScrollY);
            }
        });
    }

}
