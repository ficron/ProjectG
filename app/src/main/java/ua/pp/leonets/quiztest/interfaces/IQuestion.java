package ua.pp.leonets.quiztest.interfaces;

import ua.pp.leonets.quiztest.model.CurrentQuestion;

public interface IQuestion {

    CurrentQuestion getSelectedAnswer(); // Get selected answer from user check
    void showCorrectAnswer(); // bold correct answer text
    void disableAnswer(); // disable all check box
    void resetQuestion(); // reser all function on Question

}
