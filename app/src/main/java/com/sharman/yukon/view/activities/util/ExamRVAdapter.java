package com.sharman.yukon.view.activities.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharman.yukon.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by poten on 11/10/2015.
 */
public class ExamRVAdapter extends RecyclerView.Adapter<ExamRVAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<ExamRVInfo> examRVInfoList = Collections.emptyList();
    private OnExamRVItemClickListener onExamRVItemClickListener;

    public ExamRVAdapter(Context context, List<ExamRVInfo> examRVInfoList, OnExamRVItemClickListener onExamRVItemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.examRVInfoList = examRVInfoList;
        this.onExamRVItemClickListener = onExamRVItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = this.layoutInflater.inflate(R.layout.exam_rv_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onExamRVItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ExamRVInfo currentElement = examRVInfoList.get(i);

        viewHolder.examTitleSpan.setText(currentElement.getExamTitle());
        viewHolder.teacherImg.setImageResource(currentElement.getTeacherImgId());
        viewHolder.fileId = currentElement.getFileId();
    }

    @Override
    public int getItemCount() {
        return examRVInfoList.size();
    }



    // *ViewHolder:
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView teacherImg;
        private TextView examTitleSpan;
        private String fileId;

        public ViewHolder(View itemView, final OnExamRVItemClickListener onExamRVItemClickListener) {
            super(itemView);
            teacherImg = (ImageView) itemView.findViewById(R.id.teacherImg);
            examTitleSpan = (TextView) itemView.findViewById(R.id.examTitleSpan);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onExamRVItemClickListener.onClick(fileId);
                }
            });
        }
    }
}
