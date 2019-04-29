package com.sanquan.indoorunit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pools;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sanquan.indoorunit.R;


import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * 作者：zyf on 2018/9/1.
 * 用途：TODO
 */
public class ImagePreviewAdapter extends PagerAdapter {
    private ArrayList<String> imagePath;
    private Context context;
    private Pools.Pool<View> pool = new Pools.SimplePool<>(3);
    private com.nostra13.universalimageloader.core.ImageLoader loader;

    public ImagePreviewAdapter(ArrayList<String> imagePath, Context context) {
        this.imagePath = imagePath;
        this.context = context;
        loader= com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(context));//loader初始化
    }

    @Override
    public int getCount() {
        return imagePath == null ? 0 : imagePath.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = pool.acquire();

        if (view  == null){
             view  = LayoutInflater.from(container.getContext()).inflate(R.layout.preview_image_item,null);
        }
        PhotoView imageView = view.findViewById(R.id.img_preview);
        //加载网络图片
        loader.displayImage(imagePath.get(position),imageView);//展示图片
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        pool.release((View) object);
    }
}
