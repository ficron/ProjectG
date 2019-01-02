package ua.pp.leonets.quiztest.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Question {

    private int id;
    private String questionText, questionImage, answerA, answerB, answerC, answerD;
    private ArrayList<String> correctAnswers = new ArrayList<>();
    private boolean isImageQuestion;
    private int categoryId;

    public Question() {
    }

    public Question(int id, String questionText, String questionImage, String answerA, String answerB, String answerC, String answerD, ArrayList<String> correctAnswers, boolean isImageQuestion, int categoryId) {
        this.id = id;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswers = correctAnswers;
        this.isImageQuestion = isImageQuestion;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public ArrayList<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswer(ArrayList<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public boolean isImageQuestion() {
        return isImageQuestion;
    }

    public void setIsImageQuestion(int imageQuestion) {
        if (imageQuestion==1) isImageQuestion=true;
        else isImageQuestion=false;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
