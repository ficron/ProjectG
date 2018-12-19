package ua.pp.leonets.quiztest.common;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ua.pp.leonets.quiztest.QuestionFragment;
import ua.pp.leonets.quiztest.model.Category;
import ua.pp.leonets.quiztest.model.CurrentQuestion;
import ua.pp.leonets.quiztest.model.Question;

public class Common {

    public static final int TOTAL_TIME = 20*60*1000; // 20 minutes
    public static List<Question> questionList = new ArrayList<>();
    public static Category selectedCategory = new Category();
    public static List <CurrentQuestion> answerSheetList = new ArrayList<>();

    public static CountDownTimer countDownTimer;

    public static int right_answer_count=0;
    public static int wrong_answer_count=0;
    public static ArrayList<QuestionFragment> fragmentsList = new ArrayList<>();
    public static TreeSet<String> selected_values =  new TreeSet<>();

    public enum ANSWER_TYPE{
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }

}
