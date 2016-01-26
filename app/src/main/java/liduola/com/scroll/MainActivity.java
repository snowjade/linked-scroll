package liduola.com.scroll;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private CustomLinearLayout mCustomLinearLayout;
    private RelativeLayout mUpPart;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mCustomLinearLayout = (CustomLinearLayout) findViewById(R.id.customLinearLayout);
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
                while (position > 0){
                    s = s + "this is  test text";
                    position--;
                }
                ((ViewHolders)holder).textView.setText(s);
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
                    textView.setPadding(20,20,20,20);
                }
            }
        });
    }

}
