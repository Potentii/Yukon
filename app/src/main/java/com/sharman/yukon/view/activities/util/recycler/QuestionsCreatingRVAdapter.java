package com.sharman.yukon.view.activities.util.recycler;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.util.Validatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poten on 07/12/2015.
 */
public class QuestionsCreatingRVAdapter extends RecyclerView.Adapter<QuestionsCreatingRVAdapter.ViewHolder> implements Validatable {
    private final LayoutInflater layoutInflater;
    private List<QuestionsCreatingRVInfo> questionsCreatingRVInfoList = new ArrayList<>();
    private OnQuestionsCreatingRVItemClickListener onQuestionsCreatingRVItemClickListener;
    private OnQuestionsCreatingRVItemClickListener onQuestionsCreatingRVREmoveListener;
    private Context context;

    public QuestionsCreatingRVAdapter(Context context, List<QuestionsCreatingRVInfo> questionsCreatingRVInfoList, OnQuestionsCreatingRVItemClickListener onQuestionsCreatingRVItemClickListener, OnQuestionsCreatingRVItemClickListener onQuestionsCreatingRVREmoveListener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.questionsCreatingRVInfoList = questionsCreatingRVInfoList;
        this.onQuestionsCreatingRVItemClickListener = onQuestionsCreatingRVItemClickListener;
        this.onQuestionsCreatingRVREmoveListener = onQuestionsCreatingRVREmoveListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = this.layoutInflater.inflate(R.layout.row_question_creating, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onQuestionsCreatingRVItemClickListener, onQuestionsCreatingRVREmoveListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        QuestionsCreatingRVInfo currentQuestionsCreatingRVInfo = questionsCreatingRVInfoList.get(i);

        EMultipleAnswerType eMultipleAnswerType = currentQuestionsCreatingRVInfo.getWeightTypeAnswerStruct().getEMultipleAnswerType();

        if(eMultipleAnswerType == null){
            viewHolder.answerTypeImg.setImageResource(R.drawable.ic_answer_type_text_24dp);
        } else if(eMultipleAnswerType == EMultipleAnswerType.SINGLE_CHOICE){
            viewHolder.answerTypeImg.setImageResource(R.drawable.ic_answer_type_single_24dp);
        } else if(eMultipleAnswerType == EMultipleAnswerType.MULTIPLE_CHOICE){
            viewHolder.answerTypeImg.setImageResource(R.drawable.ic_answer_type_multiple_24dp);
        }

        viewHolder.questionTitleOut.setText((currentQuestionsCreatingRVInfo.getQuestionIndex()+1) + "-) (" + currentQuestionsCreatingRVInfo.getWeightTypeAnswerStruct().getWeight() + ") " + currentQuestionsCreatingRVInfo.getQuestion().getTitle());

        viewHolder.questionsCreatingRVInfo = currentQuestionsCreatingRVInfo;
    }


    @Override
    public int getItemCount() {
        return this.questionsCreatingRVInfoList.size();
    }


    @Override
    public boolean isValid() {
        return questionsCreatingRVInfoList != null && questionsCreatingRVInfoList.size() != 0;
    }


    // *ViewHolder:
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView answerTypeImg;
        private TextView questionTitleOut;
        private ImageButton removeBtn;

        private QuestionsCreatingRVInfo questionsCreatingRVInfo;

        public ViewHolder(View itemView, final OnQuestionsCreatingRVItemClickListener onQuestionsCreatingRVItemClickListener, final OnQuestionsCreatingRVItemClickListener onQuestionsCreatingRVREmoveListener) {
            super(itemView);

            answerTypeImg = (ImageView) itemView.findViewById(R.id.answerTypeImg);
            questionTitleOut = (TextView) itemView.findViewById(R.id.questionTitleOut);
            removeBtn = (ImageButton) itemView.findViewById(R.id.removeBtn);


            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onQuestionsCreatingRVREmoveListener.onClick(questionsCreatingRVInfo);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onQuestionsCreatingRVItemClickListener.onClick(questionsCreatingRVInfo);
                }
            });
        }
    }
}
