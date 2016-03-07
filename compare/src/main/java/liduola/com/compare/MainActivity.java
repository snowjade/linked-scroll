package liduola.com.compare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView.Adapter mRecyclerAdapter = new RecyclerView.Adapter() {
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
        };
        StaggeredGridLayoutManager staggeredGridLayoutManager = new
                StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(mRecyclerAdapter);
    }
}
