package com.starunion.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.starunion.collapselib.CollapseLayout;

/**
 * @author sz
 */
public class MainActivity extends AppCompatActivity {
    CollapseLayout collapseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collapseLayout = (CollapseLayout) findViewById(R.id.collapseLayout);

        for (int i = 0; i<5; i++){
            final ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_header, collapseLayout, false);
            if (i == 2){
                imageView.setImageResource(R.mipmap.ic_launcher_round);
            } else {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
            imageView.setTag(R.id.tag_pos_id , i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(collapseLayout.isOpened()){
                        collapseLayout.close();
                    } else {
                        collapseLayout.open();
                    }
                }
            });
            collapseLayout.addView(imageView);
        }

//        collapseLayout.setCollapseRes(R.mipmap.ic_launcher);
//        collapseLayout.setItemClickListener(new CollapseLayout.OnItemClickListener() {
//
//            @Override
//            public void onCollapseClick(View view) {
//                collapseLayout.close();
//            }
//        });
    }
}
