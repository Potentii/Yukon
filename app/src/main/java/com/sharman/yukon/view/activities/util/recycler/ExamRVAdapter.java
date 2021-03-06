package com.sharman.yukon.view.activities.util.recycler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by poten on 11/10/2015.
 */
public class ExamRVAdapter extends RecyclerView.Adapter<ExamRVAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private Vector<ExamRVInfo> examRVInfoVector = new Vector<>();
    private OnExamRVItemClickListener onExamRVItemClickListener;
    private GoogleAccountCredential credential;
    private Activity context;
    private AndroidUtil androidUtil;

    public ExamRVAdapter(Activity context, GoogleAccountCredential credential, Vector<ExamRVInfo> examRVInfoVector, OnExamRVItemClickListener onExamRVItemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.credential = credential;
        this.examRVInfoVector = examRVInfoVector;
        this.onExamRVItemClickListener = onExamRVItemClickListener;
        androidUtil = new AndroidUtil(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = this.layoutInflater.inflate(R.layout.exam_rv_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onExamRVItemClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        ExamRVInfo currentExamRVInfo = examRVInfoVector.get(i);

        viewHolder.examTitleSpan.setText(currentExamRVInfo.getExamTitle());
        viewHolder.examSubjectSpan.setText(currentExamRVInfo.getExamSubject());
        viewHolder.examDeliverDateSpan.setText(new SimpleDateFormat("dd/MM/yyyy").format(currentExamRVInfo.getExamDeliveryDate()));
        viewHolder.examRVInfo = currentExamRVInfo;

        androidUtil.formatPersonImageView_GPlus(viewHolder.teacherImg, credential, currentExamRVInfo.getTeacherId());
    }

    @Override
    public int getItemCount() {
        return this.examRVInfoVector.size();
    }


    // *ViewHolder:
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView teacherImg;
        private TextView examTitleSpan;
        private TextView examSubjectSpan;
        private TextView examDeliverDateSpan;
        private ExamRVInfo examRVInfo;

        public ViewHolder(View itemView, final OnExamRVItemClickListener onExamRVItemClickListener) {
            super(itemView);
            teacherImg = (ImageView) itemView.findViewById(R.id.teacherImg);
            examTitleSpan = (TextView) itemView.findViewById(R.id.examTitleSpan);
            examSubjectSpan = (TextView) itemView.findViewById(R.id.examSubjectSpan);
            examDeliverDateSpan = (TextView) itemView.findViewById(R.id.examDeliverDateSpan);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onExamRVItemClickListener.onClick(examRVInfo);
                }
            });
        }
    }
}
