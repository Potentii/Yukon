package com.sharman.yukon.view.activities.util.recycler;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.util.Validatable;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by poten on 28/12/2015.
 */
public abstract class QuestionCorrectionRVAdapter extends RecyclerView.Adapter<QuestionCorrectionRVAdapter.ViewHolder> implements Validatable {
    private final LayoutInflater layoutInflater;
    private final Activity activity;

    private RecyclerView recyclerView;

    @Nonnull
    private List<QuestionCorrectionRVInfo> questionCorrectionRVInfoList;

    private String invalidText = "";



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public QuestionCorrectionRVAdapter(@Nonnull Activity activity, @Nonnull List<QuestionCorrectionRVInfo> questionCorrectionRVInfoList) {
        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.questionCorrectionRVInfoList = questionCorrectionRVInfoList;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * RecyclerView.Adapter methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(this.layoutInflater.inflate(R.layout./*TODO*/, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public final int getItemCount() {
        return this.questionCorrectionRVInfoList.size();
    }

    @Override
    public final void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final QuestionCorrectionRVInfo currentQuestionCorrectionRVInfo = questionCorrectionRVInfoList.get(i);

        WeightTypeAnswerStruct weightTypeAnswerStruct = currentQuestionCorrectionRVInfo.getTeacherAnswer();
        EMultipleAnswerType eMultipleAnswerType = weightTypeAnswerStruct.getEMultipleAnswerType();
        Answer teacherAnswer = weightTypeAnswerStruct.getAnswer();
        Answer studentAnswer = currentQuestionCorrectionRVInfo.getStudentAnswer();
        Boolean correct = null;
        boolean answered = (studentAnswer.getAnswerArray().length != 0);

        if(answered){
            if(eMultipleAnswerType != null){
                correct = teacherAnswer.compareAnswerTo(studentAnswer);
            }

            // TODO change the color of the text
            if(correct == null){
                // TODO blue
            } else if(correct){
                // TODO green
            } else{
                // TODO red
            }
        } else{

            // TODO escreve o texto "not answered"
        }




        currentQuestionCorrectionRVInfo.setCorrect(correct);


        viewHolder.questionCorrectionRVInfo = currentQuestionCorrectionRVInfo;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ViewHolder:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView questionIndexOut;
        private TextView textOut;
        private ImageButton removeBtn;

        private QuestionCorrectionRVInfo questionCorrectionRVInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            questionIndexOut    = (TextView) itemView.findViewById(R.id.questionIndexOut);
            textOut             = (TextView) itemView.findViewById(R.id.textOut);
            removeBtn           = (ImageButton) itemView.findViewById(R.id.removeBtn);

            removeBtn.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(questionCorrectionRVInfo);
                }
            });
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected abstract void onItemClick(QuestionCorrectionRVInfo questionCorrectionRVInfo);

    protected abstract void onUpdated();

    public void update(){
        super.notifyDataSetChanged();
        if(recyclerView != null){
            recyclerView.setMinimumHeight((int) (getItemCount() * activity.getResources().getDimension(R.dimen.row_question_height)));
        }
        onUpdated();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Validatable methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean isValid() {
        for (QuestionCorrectionRVInfo questionCorrectionRVInfo : questionCorrectionRVInfoList) {
            if(questionCorrectionRVInfo.getCorrect() == null){
                invalidText = "Not all answers could be verified";
                return false;
            }
        }
        return true;
    }

    @Override
    public String getInvalidText() {
        return invalidText;
    }
}
