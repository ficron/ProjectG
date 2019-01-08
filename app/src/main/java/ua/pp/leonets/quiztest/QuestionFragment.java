package ua.pp.leonets.quiztest;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ua.pp.leonets.quiztest.common.Common;
import ua.pp.leonets.quiztest.interfaces.IQuestion;
import ua.pp.leonets.quiztest.model.CurrentQuestion;
import ua.pp.leonets.quiztest.model.Question;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment implements IQuestion {
    TextView txt_question_txt;
    CheckBox ckbA, ckbB, ckbC, ckbD;
    FrameLayout layout_Image;
    ProgressBar progressBar;

    Button btnConfirm;

    Question question;
    int questionIndex = -1;


    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_question, container, false);

        //Get questions
        questionIndex = getArguments().getInt("index", -1);
        question = Common.questionList.get(questionIndex);

        if (question != null) {

            layout_Image = (FrameLayout) itemView.findViewById(R.id.layout_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

            if (question.isImageQuestion()) {
                ImageView img_question = (ImageView) itemView.findViewById(R.id.image_question);
                Picasso.get().load(question.getQuestionImage()).into(img_question, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                layout_Image.setVisibility(View.GONE);
            }

            final boolean isSingle;

            if (question.getCorrectAnswers().size()>1){
                isSingle = false;
            }else {
                isSingle = true;
            }

            //View
            txt_question_txt = (TextView) itemView.findViewById(R.id.text_question_txt);
            txt_question_txt.setText(question.getQuestionText());

            ArrayList<String> answers = question.getAnswers();

            ckbA = (CheckBox) itemView.findViewById(R.id.ckbA);
            ckbA.setText(answers.get(0));
            ckbA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        if (isSingle){
                            ckbB.setChecked(false);
                            ckbC.setChecked(false);
                            ckbD.setChecked(false);
                            Log.d("TAG","Common.selected_values.size()"+Common.selected_values.size());
                        }

                        Common.selected_values.add(ckbA.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbA.getText().toString());
                    }
                }
            });


            ckbB = (CheckBox) itemView.findViewById(R.id.ckbB);
            ckbB.setText(answers.get(1));
            ckbB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        if (isSingle){
                            Common.selected_values.clear();
                            ckbA.setChecked(false);
                            ckbC.setChecked(false);
                            ckbD.setChecked(false);
                            Log.d("TAG","Common.selected_values.size()"+Common.selected_values.size());
                        }

                        Common.selected_values.add(ckbB.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbB.getText().toString());
                    }
                }
            });

            ckbC = (CheckBox) itemView.findViewById(R.id.ckbC);
            ckbC.setText(answers.get(2));
            ckbC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        if (isSingle){
                            Common.selected_values.clear();
                            ckbA.setChecked(false);
                            ckbB.setChecked(false);
                            ckbD.setChecked(false);
                            Log.d("TAG","Common.selected_values.size()"+Common.selected_values.size());
                        }

                        Common.selected_values.add(ckbC.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbC.getText().toString());
                    }
                }
            });

            ckbD = (CheckBox) itemView.findViewById(R.id.ckbD);
            ckbD.setText(answers.get(3));
            ckbD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        if (isSingle){
                            Common.selected_values.clear();
                            ckbA.setChecked(false);
                            ckbB.setChecked(false);
                            ckbC.setChecked(false);
                            Log.d("TAG","Common.selected_values.size()"+Common.selected_values.size());
                        }

                        Common.selected_values.add(ckbD.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbD.getText().toString());
                    }
                }
            });


        }


        return itemView;
    }
    @Override
    public CurrentQuestion getSelectedAnswer() {
        Log.d("CorrectAnswer", "Common.selected_values.size "+Common.selected_values.size());
        CurrentQuestion currentQuestion = new CurrentQuestion(questionIndex, Common.ANSWER_TYPE.NO_ANSWER);

        if (question!=null&&Common.selected_values.size()!=0){
            List<String> correctAnswers = question.getCorrectAnswers();

            Collections.sort(correctAnswers);
            Collections.sort(Common.selected_values);

            if (correctAnswers.equals(Common.selected_values)){
                currentQuestion.setType(Common.ANSWER_TYPE.RIGHT_ANSWER);
            }else {
                currentQuestion.setType(Common.ANSWER_TYPE.WRONG_ANSWER);
            }

        }else {
            currentQuestion.setType(Common.ANSWER_TYPE.NO_ANSWER);
        }


        Common.selected_values.clear(); //Always clear seleceted values whn compare done
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswer() {


        ArrayList<String> correctAnswers = question.getCorrectAnswers();
        Iterator<String> iter = correctAnswers.iterator();

        while (iter.hasNext()){
            String answer = iter.next();

            if (answer.equals(ckbA.getText().toString())) {
                ckbA.setTypeface(null, Typeface.BOLD);
                ckbA.setTextColor(Color.GREEN);
            }
            if (answer.equals(ckbB.getText().toString())) {
                ckbB.setTypeface(null, Typeface.BOLD);
                ckbB.setTextColor(Color.GREEN);
            }
            if (answer.equals(ckbC.getText().toString())) {
                ckbC.setTypeface(null, Typeface.BOLD);
                ckbC.setTextColor(Color.GREEN);
            }
            if (answer.equals(ckbD.getText().toString())) {
                ckbD.setTypeface(null, Typeface.BOLD);
                ckbD.setTextColor(Color.GREEN);
            }
        }

    }

    @Override
    public void disableAnswer() {
        ckbA.setEnabled(false);
        ckbB.setEnabled(false);
        ckbC.setEnabled(false);
        ckbD.setEnabled(false);

    }

    @Override
    public void resetQuestion() {
        //Enable Checkbox
        ckbA.setEnabled(true);
        ckbB.setEnabled(true);
        ckbC.setEnabled(true);
        ckbD.setEnabled(true);


        //Remove all selected
        ckbA.setChecked(false);
        ckbB.setChecked(false);
        ckbC.setChecked(false);
        ckbD.setChecked(false);

        //Remove all bold on text

        ckbA.setTypeface(null, Typeface.NORMAL);
        ckbA.setTextColor(Color.BLACK);

        ckbB.setTypeface(null, Typeface.NORMAL);
        ckbB.setTextColor(Color.BLACK);

        ckbC.setTypeface(null, Typeface.NORMAL);
        ckbC.setTextColor(Color.BLACK);

        ckbD.setTypeface(null, Typeface.NORMAL);
        ckbD.setTextColor(Color.BLACK);
    }




}
