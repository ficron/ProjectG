package ua.pp.leonets.quiztest.interfaces;

import java.util.List;

import ua.pp.leonets.quiztest.model.Question;

public interface MyQuestionListCallback {

    void setQuestionList (List<Question> questionList);
}
