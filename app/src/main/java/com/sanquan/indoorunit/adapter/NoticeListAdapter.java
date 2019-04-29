package com.sanquan.indoorunit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sanquan.indoorunit.R;
import com.sanquan.indoorunit.bean.AppHomeInfoBean;

import java.util.List;

/**
 * 作者：zyf on 2018/9/4.
 * 用途：TODO
 */
public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder> {
    private List<AppHomeInfoBean.NoticeBean> noticeBeanList;

    public NoticeListAdapter(List<AppHomeInfoBean.NoticeBean> noticeBeanList) {
        this.noticeBeanList = noticeBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppHomeInfoBean.NoticeBean noticeBean = noticeBeanList.get(position);
        holder.tvNoticeTime.setText(noticeBean.getDate_time());
        holder.tvNoticeOwer.setText("发布者: "+noticeBean.getOwner());
        holder.tvNoticeContent.setText("内容: "+noticeBean.getContent());
        holder.tvNoticeTitle.setText(noticeBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return noticeBeanList == null ? 0 : noticeBeanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNoticeContent;
        private TextView tvNoticeTitle;
        private TextView tvNoticeOwer;
        private TextView tvNoticeTime;
        public ViewHolder(View itemView) {
            super(itemView);
            tvNoticeContent = itemView.findViewById(R.id.notice_content);
            tvNoticeTitle = itemView.findViewById(R.id.notice_title);
            tvNoticeOwer = itemView.findViewById(R.id.notice_ower);
            tvNoticeTime = itemView.findViewById(R.id.notice_time);
        }
    }
}
