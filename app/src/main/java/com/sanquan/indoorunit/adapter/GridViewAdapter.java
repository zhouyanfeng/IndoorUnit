package com.sanquan.indoorunit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanquan.indoorunit.R;

import java.text.BreakIterator;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * 作者：zyf on 2018/9/21.
 * 用途：TODO
 */
public class GridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private String[] titles = {"通知公告","呼叫中心","房屋报修","附近商圈","意见反馈","物业账单","开门记录","系统设置"};
    private int[] imgIds = {R.mipmap.notices,R.mipmap.call_center,R.mipmap.maintain2,R.mipmap.shopping,R.mipmap.check_feedback,R.mipmap.cost,R.mipmap.record,R.mipmap.setting};
    private int[] bgColor = {R.drawable.notices_bg,R.drawable.call_bg,R.drawable.maintain_bg,R.drawable.account_bg};
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == R.layout.grid_item_one) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_one,parent,false);
            view.setOnClickListener(this);
            return new ViewHolderOne(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_two,parent,false);
            view.setOnClickListener(this);
            return new ViewHolderTwo(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position < 4){
            ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
            viewHolderOne.imgGrid.setImageResource(imgIds[position]);
            viewHolderOne.tvTitleGrid.setText(titles[position]);
            holder.itemView.setBackgroundResource(bgColor[position]);
            holder.itemView.setTag(position);
        }else {
            ViewHolderTwo viewHolderTwo = (ViewHolderTwo) holder;
            viewHolderTwo.img.setImageResource(imgIds[position]);
            viewHolderTwo.tvTitle.setText(titles[position]);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (itemOnClickListener != null){
            itemOnClickListener.ItemOnClick(position);
        }
    }

    public class ViewHolderOne extends RecyclerView.ViewHolder{

        @BindView(R.id.img_grid)
        ImageView imgGrid;
        @BindView(R.id.tv_title_grid)
        TextView tvTitleGrid;
        public ViewHolderOne(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public class ViewHolderTwo extends RecyclerView.ViewHolder{

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        public ViewHolderTwo(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private ItemOnClickListener itemOnClickListener;

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    public interface ItemOnClickListener{
        void ItemOnClick(int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 4){
            return R.layout.grid_item_one;
        }else {
            return R.layout.grid_item_two;
        }
    }
}
