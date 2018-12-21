package ua.pp.leonets.quiztest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import ua.pp.leonets.quiztest.R;
import ua.pp.leonets.quiztest.common.Common;
import ua.pp.leonets.quiztest.model.CurrentQuestion;

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.MyViewHolder> {
    Context context;
    List<CurrentQuestion> currentQuestionList;

    public ResultGridAdapter(Context context, List<CurrentQuestion> currentQuestionList) {
        this.context = context;
        this.currentQuestionList = currentQuestionList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button btn_question;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_question = (Button) itemView.findViewById(R.id.btn_question);
            btn_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // When User click QuestionButton we will get back to Question Activity to show question
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(new Intent(Common.KEY_BACK_FROM_RESULT).putExtra(Common.KEY_GO_TO_QUESTION,
                                    currentQuestionList.get(getAdapterPosition()).getQuestionIndex()));
                }
            });
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_result_item, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Drawable img;

        //Change color base on Result

        myViewHolder.btn_question.setText(new StringBuilder("Question").append(currentQuestionList.get(i)
                .getQuestionIndex() + 1));

        if (currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER) {
            myViewHolder.btn_question.setBackgroundColor(Color.parseColor("#ff99cc00"));
            img = context.getResources().getDrawable(R.drawable.ic_check_white_24dp);
            myViewHolder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        }
        else if (currentQuestionList.get(i).getType() == Common.ANSWER_TYPE.WRONG_ANSWER) {
            myViewHolder.btn_question.setBackgroundColor(Color.parseColor("#ffbc0000"));
            img = context.getResources().getDrawable(R.drawable.ic_clear_white_24dp);
            myViewHolder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        }
        else  {
            img = context.getResources().getDrawable(R.drawable.ic_error_outline_white_24dp);
            myViewHolder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        }

    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }


}
