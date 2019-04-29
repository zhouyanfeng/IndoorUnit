package com.sanquan.indoorunit.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.app.MainApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        ButterKnife.bind(this);
        String title = getIntent().getStringExtra("title");
        textView.setText(title);
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}
