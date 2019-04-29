package com.sanquan.indoorunit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.bean.FeedbackBean;


import java.util.List;

/**
 * 作者：zyf on 2018/8/31.
 * 用途：TODO
 */
public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> implements View.OnClickListener {


    private Context context;
    private List<FeedbackBean.ResBean> itemBeanList;

    public FeedbackAdapter(List<FeedbackBean.ResBean> itemBeanList) {
        this.itemBeanList = itemBeanList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item,parent,false);
        context = parent.getContext();
        return new FeedbackViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackBean.ResBean item = itemBeanList.get(position);
        holder.tvContent.setText("报修内容："+item.getEvent_content());
        String msg = item.getEvent_msg().equals("") ? "无反馈信息" : item.getEvent_msg();
        holder.tvMsg.setText("管理员回复: "+msg);
        holder.tvTime.setText(item.getEvent_create_time());
        holder.llImageContainer.removeAllViews();
        holder.llImageContainer.setTag(position);
        holder.tvHandlerState.setTextColor(R.color.gray);
        setTextViewIconColor(holder.tvHandlerState,item.getEvent_step());
        int width = (int) context.getResources().getDimension(R.dimen.list_image_item);
        int i = 0;
        for (String s : item.getEvent_img()) {
            ImageView imageView = new ImageView(context);
            imageView.setTag(R.id.imagePositon,i);
            imageView.setTag(R.id.imageContainerPosition,position);
            imageView.setOnClickListener(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
            params.setMargins(0,0,10,0);
            imageView.setLayoutParams(params);
            Glide.with(context).load(s).placeholder(R.mipmap.error_img).error(R.mipmap.error_img).into(imageView);
            holder.llImageContainer.addView(imageView);
            i++;
        }
    }
        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.M)
        private void setTextViewIconColor(TextView textView,String step) {
           if (step.equals("未处理")){
               Drawable[] compoundDrawables = textView.getCompoundDrawables();
               @SuppressLint("ResourceAsColor")
               PorterDuffColorFilter cf = new PorterDuffColorFilter(context.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
               compoundDrawables[0].setColorFilter(cf);
               textView.setTextColor(context.getResources().getColor(R.color.red));
           }
            textView.setText(step);
    }
    @Override
    public int getItemCount() {
        return itemBeanList == null ? 0 : itemBeanList.size();
    }

    @Override
    public void onClick(View v) {
        int imagePosition = (int) v.getTag(R.id.imagePositon);
        int itemPosition = (int) v.getTag(R.id.imageContainerPosition);
        if (onImageItemClickListener != null)
            onImageItemClickListener.onImageItemClickListener(itemPosition,imagePosition);
    }


    public class FeedbackViewHolder extends RecyclerView.ViewHolder{

        private TextView tvContent;
        private TextView tvMsg;
        private TextView tvHandlerState;
        private LinearLayout llImageContainer;
        private TextView tvTime;
        public FeedbackViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvMsg = itemView.findViewById(R.id.tv_msg);
            tvHandlerState = itemView.findViewById(R.id.handler_state);
            llImageContainer = itemView.findViewById(R.id.ll_img_container);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    private OnImageItemClickListener onImageItemClickListener;

    public void setOnImageItemClickListener(OnImageItemClickListener onImageItemClickListener) {
        this.onImageItemClickListener = onImageItemClickListener;
    }

    public interface OnImageItemClickListener{
        void onImageItemClickListener(int itemPosition, int imagePosition);
    }
}
