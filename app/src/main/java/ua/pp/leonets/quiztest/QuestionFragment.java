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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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


            //View
            txt_question_txt = (TextView) itemView.findViewById(R.id.text_question_txt);
            txt_question_txt.setText(question.getQuestionText());


            ckbA = (CheckBox) itemView.findViewById(R.id.ckbA);
            ckbA.setText(question.getAnswerA());
            ckbA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        Common.selected_values.add(ckbA.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbA.getText().toString());
                    }
                }
            });


            ckbB = (CheckBox) itemView.findViewById(R.id.ckbB);
            ckbB.setText(question.getAnswerB());
            ckbB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        Common.selected_values.add(ckbB.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbB.getText().toString());
                    }
                }
            });

            ckbC = (CheckBox) itemView.findViewById(R.id.ckbC);
            ckbC.setText(question.getAnswerC());
            ckbC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        Common.selected_values.add(ckbC.getText().toString());
                    } else {
                        Common.selected_values.remove(ckbC.getText().toString());
                    }
                }
            });

            ckbD = (CheckBox) itemView.findViewById(R.id.ckbD);
            ckbD.setText(question.getAndwerD());
            ckbD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
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
        StringBuilder result = new StringBuilder();
        if (Common.selected_values.size() > 1)
        {

            /*
            if multichose
            split anwer to array
            arr[0] A.New York
            arr[1] B.Paris
             */

            Object[] arrayAnswer = Common.selected_values.toArray();

            for (int i = 0; i < arrayAnswer.length; i++) {
                String mAnswer = (String)arrayAnswer[i];
                if (i >0) {
                    result.append(mAnswer.substring(0, 1));
                } else {
                    result.append(mAnswer.substring(0, 1).concat(","));
                }
            }


        } else if (Common.selected_values.size() == 1) {
            //if obnly one choise
            Object[] arrayAnswer = Common.selected_values.toArray();
            String answer = (String) arrayAnswer[0];
            result.append(((String) arrayAnswer[0]).substring(0, 1));
        }

        Log.d("CorrectAnswer","result: "+result.toString());

        if (question != null) {
            //Compare user answer with correct answer
            if (!TextUtils.isEmpty(result)) {
                if (result.toString().equals(question.getCorrectAnswer())) {
                    currentQuestion.setType(Common.ANSWER_TYPE.RIGHT_ANSWER);
                } else {
                    currentQuestion.setType(Common.ANSWER_TYPE.WRONG_ANSWER);
                }
            } else {
                currentQuestion.setType(Common.ANSWER_TYPE.NO_ANSWER);
            }

        } else {
            Toast.makeText(getContext(), "Cannot get question", Toast.LENGTH_LONG).show();
            currentQuestion.setType(Common.ANSWER_TYPE.NO_ANSWER);
        }

        Common.selected_values.clear(); //Always clear seleceted values whn compare done
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswer() {
//Bold correct answer
        //Pattern : A , B
        String[] correctAnser = question.getCorrectAnswer().split(",");

        for (String answer : correctAnser) {
            Log.d("CorrectAnswer","Type"+getSelectedAnswer().getType());
            Log.d("CorrectAnswer",question.getQuestionText()+" correctAnswer"+answer);


            if (answer.equals("A")) {
                ckbA.setTypeface(null, Typeface.BOLD);
                ckbA.setTextColor(Color.RED);
            }
            if (answer.equals("B")) {
                ckbB.setTypeface(null, Typeface.BOLD);
                ckbB.setTextColor(Color.RED);
            }
            if (answer.equals("C")) {
                ckbC.setTypeface(null, Typeface.BOLD);
                ckbC.setTextColor(Color.RED);
            }
            if (answer.equals("D")) {
                ckbD.setTypeface(null, Typeface.BOLD);
                ckbD.setTextColor(Color.RED);
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
