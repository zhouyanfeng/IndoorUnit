package com.sanquan.indoorunit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.bean.CenterMachinesBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：zyf on 2019/4/26.
 * 用途：TODO
 */
public class CenterMachinesAdapter extends RecyclerView.Adapter<CenterMachinesAdapter.ViewHolder> implements View.OnClickListener {
    private List<CenterMachinesBean.CenterListBean> centerListBeanList;

    public CenterMachinesAdapter(List<CenterMachinesBean.CenterListBean> centerListBeanList) {
        this.centerListBeanList = centerListBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.center_machine_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(centerListBeanList.get(position));
        holder.itemView.setOnClickListener(this);
        holder.tvDoorCreateTime.setText("创建时间: "+centerListBeanList.get(position).getDoor_time());
        holder.tvDoorName.setText("名     称:"+centerListBeanList.get(position).getDoor_name());
    }

    @Override
    public int getItemCount() {
        return centerListBeanList == null ? 0 : centerListBeanList.size();
    }

    @Override
    public void onClick(View v) {
        CenterMachinesBean.CenterListBean centerListBean = (CenterMachinesBean.CenterListBean) v.getTag();
        if (itemClickListener != null) {
            itemClickListener.onItemClickListener(centerListBean);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_door_name)
        TextView tvDoorName;
        @BindView(R.id.tv_door_create_time)
        TextView tvDoorCreateTime;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setCenterListBeanList(List<CenterMachinesBean.CenterListBean> centerListBeanList) {
        this.centerListBeanList = centerListBeanList;
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ItemClickListener itemClickListener;
    public interface ItemClickListener{
        void onItemClickListener(CenterMachinesBean.CenterListBean centerListBean);
    }
}
