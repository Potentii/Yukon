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

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by poten on 07/12/2015.
 */

public abstract class QuestionCreationRVAdapter extends RecyclerView.Adapter<QuestionCreationRVAdapter.ViewHolder> implements Validatable {
    private final LayoutInflater layoutInflater;
    private final Context context;

    private RecyclerView recyclerView;

    @Nonnull
    private List<QuestionCreationRVInfo> questionCreationRVInfoList;

    private String invalidText = "";



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public QuestionCreationRVAdapter(@Nonnull Context context, @Nonnull List<QuestionCreationRVInfo> questionCreationRVInfoList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.questionCreationRVInfoList = questionCreationRVInfoList;
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
        return this.questionCreationRVInfoList.size();
    }

    @Override
    public final void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final QuestionCreationRVInfo currentQuestionCreationRVInfo = questionCreationRVInfoList.get(i);

        viewHolder.questionIndexOut.setText(String.valueOf(currentQuestionCreationRVInfo.getIndex()+1));
        viewHolder.textOut.setText("[" + new DecimalFormat("#.00").format(currentQuestionCreationRVInfo.getQuestion().getWeight()) + "] " + currentQuestionCreationRVInfo.getQuestion().getTitle());

        viewHolder.questionCreationRVInfo = currentQuestionCreationRVInfo;
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

        private QuestionCreationRVInfo questionCreationRVInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            questionIndexOut    = (TextView) itemView.findViewById(R.id.questionIndexOut);
            textOut             = (TextView) itemView.findViewById(R.id.textOut);
            removeBtn           = (ImageButton) itemView.findViewById(R.id.removeBtn);


            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemRemove(questionCreationRVInfo);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(questionCreationRVInfo);
                }
            });
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected abstract void onItemClick(QuestionCreationRVInfo questionCreationRVInfo);

    protected abstract void onItemRemove(QuestionCreationRVInfo questionCreationRVInfo);

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
        if(questionCreationRVInfoList == null || questionCreationRVInfoList.size()==0){
            try {
                invalidText = context.getResources().getString(R.string.output_invalidField_questionsCreateRV_empty);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public String getInvalidText() {
        return invalidText;
    }
}
