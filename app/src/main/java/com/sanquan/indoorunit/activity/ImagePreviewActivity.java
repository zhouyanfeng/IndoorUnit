package com.sanquan.indoorunit.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.adapter.ImagePreviewAdapter;
import com.sanquan.indoorunit.adapter.ViewPagerFixed;
import com.sanquan.indoorunit.app.MainApplication;

import java.util.ArrayList;

public class ImagePreviewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ArrayList<String> imagePath;
    private ViewPagerFixed viewPager;
    private ImagePreviewAdapter adapter;
    private int imagePosition;
    private TextView tvPreviewCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview2);
        initData();
        initView();
        tvPreviewCount.setText("("+(imagePosition+1)+"/"+imagePath.size()+")");
    }
    private void initData(){
        Intent intent = getIntent();
        imagePath = intent.getStringArrayListExtra("imagePath");
        imagePosition = intent.getIntExtra("imagePosition",0);
    }

    private void initView(){
        tvPreviewCount = findViewById(R.id.tv_preview_count);
        viewPager = findViewById(R.id.viewpager);
        adapter = new ImagePreviewAdapter(imagePath,this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imagePosition);
        viewPager.setOnPageChangeListener(this);

    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tvPreviewCount.setText("("+(position+1)+"/"+imagePath.size()+")");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
