package com.sanquan.indoorunit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.bean.OpenDoorRecordBean;


import java.util.List;

/**
 * 作者：zyf on 2018/8/4.
 * 用途：TODO
 */
public class OpenDoorRecordAdapter extends RecyclerView.Adapter<OpenDoorRecordAdapter.OpenDoorRecordViewHolder> {

    private Context context;
    private List<OpenDoorRecordBean.UnlockLogBean> unlockLogBeanList;

    public OpenDoorRecordAdapter(Context context, List<OpenDoorRecordBean.UnlockLogBean> unlockLogBeanList) {
        this.unlockLogBeanList = unlockLogBeanList;
        this.context = context;
    }

    @NonNull
    @Override
    public OpenDoorRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_door_record_item,parent,false);
        return new OpenDoorRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenDoorRecordViewHolder holder, int position) {
        OpenDoorRecordBean.UnlockLogBean unlockLogBean = unlockLogBeanList.get(position);
        Glide.with(context)
                .load(unlockLogBean.getFace_url())
                .error(R.mipmap.no_pictures)
                .placeholder(R.mipmap.no_pictures)
                .into(holder.imgFace);
        holder.tvDoorName.setText(unlockLogBean.getDoor_name());
        holder.tvUnlockTime.setText(unlockLogBean.getUnlock_time());
        String accessMode = unlockLogBean.getAccess_mode();

        switch (accessMode) {
            case "1":
                accessMode = "刷卡";
                break;
            case "2":
                accessMode = "手机远程开门";
                break;
            case "3":
                accessMode = "密码开门";
                break;
            case "4":
                accessMode = "人工开门";
                break;
            case "5":
                accessMode = "WiFi开门";
                break;
            case "6":
                accessMode = "刷脸开门";
                break;
            case "7":
                accessMode = "指纹开门";
                break;
        }
        holder.tvAccessMode.setText(accessMode);
    }

    @Override
    public int getItemCount() {
        return unlockLogBeanList == null?0:unlockLogBeanList.size();
    }

    public static class OpenDoorRecordViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgFace;
        private TextView tvDoorName;
        private TextView tvUnlockTime;
        private TextView tvAccessMode;
        public OpenDoorRecordViewHolder(View itemView) {
            super(itemView);
            imgFace = itemView.findViewById(R.id.img_face);
            tvAccessMode = itemView.findViewById(R.id.tv_access_mode);
            tvUnlockTime = itemView.findViewById(R.id.tv_unlock_time);
            tvDoorName = itemView.findViewById(R.id.tv_door_name);
        }
    }
}
