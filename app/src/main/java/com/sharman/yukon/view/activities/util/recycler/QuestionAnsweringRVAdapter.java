package com.sharman.yukon.view.activities.util.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.util.Validatable;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by poten on 17/12/2015.
 */
public abstract class QuestionAnsweringRVAdapter extends RecyclerView.Adapter<QuestionAnsweringRVAdapter.ViewHolder> implements Validatable {
    private final LayoutInflater layoutInflater;
    private final Context context;

    private RecyclerView recyclerView;

    @Nonnull
    private List<QuestionAnsweringRVInfo> questionAnsweringRVInfoList;

    private String invalidText = "";



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public QuestionAnsweringRVAdapter(@Nonnull Context context, @Nonnull List<QuestionAnsweringRVInfo> questionAnsweringRVInfoList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.questionAnsweringRVInfoList = questionAnsweringRVInfoList;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * RecyclerView.Adapter methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(this.layoutInflater.inflate(R.layout.row_question, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public final int getItemCount() {
        return this.questionAnsweringRVInfoList.size();
    }

    @Override
    public final void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final QuestionAnsweringRVInfo currentQuestionAnsweringRVInfo = questionAnsweringRVInfoList.get(i);

        viewHolder.questionIndexOut.setText(String.valueOf(currentQuestionAnsweringRVInfo.getIndex()+1));

        String[] answerArray;
        String text = "";

        try{
            answerArray = currentQuestionAnsweringRVInfo.getAnswer().getAnswerArray();
        } catch (NullPointerException e){
            answerArray = new String[0];
        }

        if(answerArray.length == 0){
            // *Not answered yet:
            text = context.getResources().getString(R.string.output_examAnswering_questionNotAnsweredYet);
        } else{
            // *It has an answer:
            for (int j = 0; j < answerArray.length; j++) {
                text += answerArray[j];
                text += (answerArray.length > (j+1)) ? ", ":"";
            }
        }
        viewHolder.textOut.setText(text);

        viewHolder.questionAnsweringRVInfo = currentQuestionAnsweringRVInfo;
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

        private QuestionAnsweringRVInfo questionAnsweringRVInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            questionIndexOut    = (TextView) itemView.findViewById(R.id.questionIndexOut);
            textOut             = (TextView) itemView.findViewById(R.id.textOut);
            removeBtn           = (ImageButton) itemView.findViewById(R.id.removeBtn);

            removeBtn.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(questionAnsweringRVInfo);
                }
            });
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected abstract void onItemClick(QuestionAnsweringRVInfo questionAnsweringRVInfo);

    public void update(){
        super.notifyDataSetChanged();
        if(recyclerView != null){
            recyclerView.setMinimumHeight((int) (getItemCount() * context.getResources().getDimension(R.dimen.row_question_height)));
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Validatable methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean isValid() {
        for (QuestionAnsweringRVInfo questionAnsweringRVInfo : questionAnsweringRVInfoList) {
            try {
                if(questionAnsweringRVInfo.getAnswer().getAnswerArray().length == 0){
                    try {
                        invalidText = context.getResources().getString(R.string.output_invalidField_questionAnsweringRV_unansweredQuestions);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    return false;
                }
            } catch (NullPointerException e){
                try {
                    invalidText = context.getResources().getString(R.string.output_invalidField_questionAnsweringRV_unansweredQuestions);
                } catch (NullPointerException e2){
                    e2.printStackTrace();
                }
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
