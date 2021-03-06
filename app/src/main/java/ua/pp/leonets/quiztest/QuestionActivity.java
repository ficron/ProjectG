package ua.pp.leonets.quiztest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ua.pp.leonets.quiztest.adapter.AnswerSheetAdapter;
import ua.pp.leonets.quiztest.adapter.AnswerSheetHelperAdapter;
import ua.pp.leonets.quiztest.adapter.QuestionFragmentAdapter;
import ua.pp.leonets.quiztest.common.Common;
import ua.pp.leonets.quiztest.dbhelper.DBHelper;
import ua.pp.leonets.quiztest.dbhelper.OnlineDBHelper;
import ua.pp.leonets.quiztest.interfaces.MyQuestionListCallback;
import ua.pp.leonets.quiztest.model.CurrentQuestion;
import ua.pp.leonets.quiztest.model.Question;

public class QuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CODE_GET_RESULT = 9999;
    int time_play = Common.TOTAL_TIME;
    boolean isAnswerModeView = false;

    TextView txt_right_answer, txt_timer, txt_wrong_answer;

    RecyclerView answer_sheet_view;
    AnswerSheetAdapter answerSheetAdapter;
    AnswerSheetHelperAdapter answerSheetHelperAdapter;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onDestroy() {
        if (Common.countDownTimer != null) {
            Common.countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Common.right_answer_count = Common.wrong_answer_count = 0;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Common.selectedCategory.getName());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        takeQuestion();

    }


    private void countCorrectAnswer() {
        Common.right_answer_count = Common.wrong_answer_count = 0;

        Log.d("TAG", "Common.answerSheetList.size()" + Common.answerSheetList.size());

        int count = 0;
        for (CurrentQuestion item : Common.answerSheetList) {
            if (item == null) count++;
        }
        Log.d("TAG", "Common.answerSheetList.NULLsize " + count);


        for (CurrentQuestion item : Common.answerSheetList) {
            if (item.getType() == Common.ANSWER_TYPE.RIGHT_ANSWER) {
                Common.right_answer_count++;
            } else if (item.getType() == Common.ANSWER_TYPE.WRONG_ANSWER) {
                Common.wrong_answer_count++;
            }

        }
    }

    private void genFragmentList() {
        Common.fragmentsList.clear();

        for (int i = 0; i < Common.questionList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);

            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);

            Common.fragmentsList.add(fragment);
        }

    }


    private void countTimer() {

        if (Common.countDownTimer != null) {
            Common.countDownTimer.cancel();
        }

        Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME, 1000) {
            @Override
            public void onTick(long l) {
                txt_timer.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MILLISECONDS.toMinutes(l) * 60
                ));
                time_play -= 1000;
            }

            @Override
            public void onFinish() {
                //Finish QUIZ
                finishGame();
            }
        }.start();
    }


    /**
     * TODO: Вирішити, що робити з б/д
     * чи онлайн чи SQL Lite
     */

    private void takeQuestion() {
        Common.isOnlineMode = true;

        if (Common.isOnlineMode) {
            takeQuestionFromOnlineDB();
        } else {
            takeQuestionFromSQLLiteDB();
        }
    }

    private void takeQuestionFromSQLLiteDB() {
        Common.questionList = DBHelper.getInstance(this).getQuestionByCategory(Common.selectedCategory.getId());
        if (Common.questionList.size() == 0) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Ooops")
                    .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                    .setDescription("We don't have any questions in this " + Common.selectedCategory.getName() + " category")
                    .setPositiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        } else {
                        /*
            Generate answerSheet item from question
            30 q = 30 answer sheet item
            so 1 q = 1 a.s.i.
             */

            if (Common.answerSheetList.size() > 0) Common.answerSheetList.clear();

            for (int i = 0; i < Common.questionList.size(); i++) {
                Common.answerSheetList.add(new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER));
            }
        }
        setupQuestion();
    }


    private void takeQuestionFromOnlineDB() {
        OnlineDBHelper.getInstance(QuestionActivity.this,
                FirebaseDatabase.getInstance())
                .readData(new MyQuestionListCallback() {
                    @Override
                    public void setQuestionList(List<Question> questionList) {
                        Common.questionList.clear();
                        Common.questionList = questionList;

                        if (Common.questionList.size() == 0) {
                            new MaterialStyledDialog.Builder(QuestionActivity.this)
                                    .setTitle("Ooops")
                                    .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                                    .setDescription("Немає жодного питання в цій категорії" + Common.selectedCategory.getName() + " category")
                                    .setPositiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).show();
                        } else {

                        /*
            Generate answerSheet item from question
            30 q = 30 answer sheet item
            so 1 q = 1 a.s.i.
             */
                            if (Common.answerSheetList.size() > 0) {
                                Common.answerSheetList.clear();
                            }


                            for (int i = 0; i < Common.questionList.size(); i++) {
                                Common.answerSheetList.add(new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER));
                            }

                        }
                        setupQuestion();
                    }
                }, Common.selectedCategory.getName().replace(" ", "").replace("/", "_"));
    }


    private void setupQuestion() {
        if (Common.questionList.size() > 0) {

            // Show TextView rightAnswer and TextView  - timer
            txt_right_answer = (TextView) findViewById(R.id.txt_question_right);
            txt_right_answer.setVisibility(View.VISIBLE);
            txt_right_answer.setText(new StringBuilder(String.format("%d/%d", Common.right_answer_count, Common.questionList.size())));


            txt_timer = (TextView) findViewById(R.id.txt_timer);
            txt_timer.setVisibility(View.VISIBLE);

            countTimer();


            /// View
            answer_sheet_view = (RecyclerView) findViewById(R.id.grid_answer);
            answer_sheet_view.setHasFixedSize(true);

            //if questionList >5 we will sperate 2 rows
            if (Common.questionList.size() > 2) {
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.questionList.size() / 2));
            }
            answerSheetAdapter = new AnswerSheetAdapter(this, Common.answerSheetList);
            answerSheetHelperAdapter = new AnswerSheetHelperAdapter(this, Common.answerSheetList);

            answer_sheet_view.setAdapter(answerSheetAdapter);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabLayout = (TabLayout) findViewById(R.id.slidding_tabs);

            genFragmentList();

            QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(
                    getSupportFragmentManager(),
                    this,
                    Common.fragmentsList);
            viewPager.setAdapter(questionFragmentAdapter);
            viewPager.setOffscreenPageLimit(Common.questionList.size());

            tabLayout.setupWithViewPager(viewPager);

            //Event
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                int SCROOLING_RIGHT = 0;
                int SCROOLING_LEFT = 1;
                int SCROOLING_UNDETERMINED = 2;

                int currentScroolDirection = 2;

                private void setScroolingDirection(float positionOffset) {
                    if ((1 - positionOffset) >= 0.5) {
                        this.currentScroolDirection = SCROOLING_RIGHT;
                    } else if ((1 - positionOffset) <= 0.5) {
                        this.currentScroolDirection = SCROOLING_LEFT;
                    }
                }

                private boolean isScroolDirectionUndermined() {
                    return currentScroolDirection == SCROOLING_UNDETERMINED;
                }

                private boolean isScrollingRight() {
                    return currentScroolDirection == SCROOLING_RIGHT;
                }

                private boolean isScroolingLeft() {
                    return currentScroolDirection == SCROOLING_LEFT;
                }

                @Override
                public void onPageScrolled(int i, float v, int i1) {
                    if (isScroolDirectionUndermined()) {
                        setScroolingDirection(v);
                    }
                }

                @Override
                public void onPageSelected(int i) {
                    QuestionFragment questionFragment;
                    int position = 0;

                    if (i > 0) {
                        if (isScrollingRight()) {
                            //if user scrool to righr, get previous fragment to calculate result
                            questionFragment = Common.fragmentsList.get(i - 1);
                            position = i - 1;
                        } else if (isScroolingLeft()) {
                            //if user scrool to left, get next fragment to calculate result
                            questionFragment = Common.fragmentsList.get(i + 1);
                            position = i + 1;
                        } else {
                            questionFragment = Common.fragmentsList.get(position);
                        }

                    } else {
                        questionFragment = Common.fragmentsList.get(0);
                        position = 0;
                    }

                    //For showing correct answert call method here

                    CurrentQuestion question_state = questionFragment.getSelectedAnswer();

                    //Log.d("CorrectAnswer", "Common.answerSheetList.size()" + Common.answerSheetList.size());

                    Log.d("CorrectAnswer", "onPageSelected : " + "\n"
                            + Common.questionList.get(position).getQuestionText() + "\n"
                            + Common.answerSheetList.get(position).getType() + "\n"
                            + "i: " + i+" position "+position);

                    if (Common.answerSheetList.get(position).getType() == Common.ANSWER_TYPE.NO_ANSWER) {
                        Common.answerSheetList.set(position, question_state); // Set question answer for answersheet
                        answerSheetAdapter.notifyDataSetChanged(); // Change color in answerSheet
                        answerSheetHelperAdapter.notifyDataSetChanged();

                        countCorrectAnswer();
                        txt_right_answer.setText(new StringBuilder(String.format("%d", Common.right_answer_count))
                                .append("/")
                                .append(String.format("%d", Common.questionList.size())).toString());
                        txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

                        Log.d("CorrectAnswer", "getType() " + question_state.getType());
                        Log.d("CorrectAnswer", "  ------");

                    }


                    if (question_state.getType() != Common.ANSWER_TYPE.NO_ANSWER) {
                        questionFragment.showCorrectAnswer();
                        questionFragment.disableAnswer();
                    }


                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    if (i == ViewPager.SCROLL_STATE_IDLE) {
                        this.currentScroolDirection = SCROOLING_UNDETERMINED;
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_finish_game) {
            if (!isAnswerModeView) {
                new MaterialStyledDialog.Builder(this)
                        .setTitle("Завершити?")
                        .setIcon(R.drawable.ic_mood_black_24dp)
                        .setDescription("Ви дійсно хочете завершити?")
                        .setNegativeText("Ні")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Так")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finishGame();
                            }
                        }).show();


            } else {
                finishGame();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_wrong_answer);
        ConstraintLayout constraintLayout = (ConstraintLayout) item.getActionView();

        txt_wrong_answer = (TextView) constraintLayout.findViewById(R.id.txt_wrong_answer);
        txt_wrong_answer.setText(String.valueOf(0));


        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_done) {
            finishGame();
        } else if (id == R.id.menu_adjust) {
            Toast.makeText(this, "menu_adjust В розробці", Toast.LENGTH_LONG).show();
        } else if (id == R.id.menu_send_error) {
            String aEmailList[] = {"authentikos.supp@gmail.com"};
            Question currentQuestion = Common.questionList.get(viewPager.getCurrentItem());
            composeEmail(aEmailList, currentQuestion);
            //формируем email - получателей

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void composeEmail(String[] addresses, Question question) {
        String subject = "Помилка у питанні  ID" + question.getId();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "Питання: " + question.getQuestionText());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Log.d("TAG", "Activity.RESULT_OK");
            String action = data.getStringExtra("action");
            if (action == null || TextUtils.isEmpty(action)) {
                int questionNum = data.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1);
                viewPager.setCurrentItem(questionNum);

                isAnswerModeView = true;
                Common.countDownTimer.cancel();

                txt_wrong_answer.setVisibility(View.GONE);
                txt_right_answer.setVisibility(View.GONE);
                txt_timer.setVisibility(View.GONE);
            } else {
                if (action.equals("viewquizanswer")) {
                    Log.d("TAG", "viewquizanswer");
                    viewPager.setCurrentItem(0);

                    isAnswerModeView = true;
                    Common.countDownTimer.cancel();

                    txt_wrong_answer.setVisibility(View.GONE);
                    txt_right_answer.setVisibility(View.GONE);
                    txt_timer.setVisibility(View.GONE);

                    for (int i = 0; i < Common.fragmentsList.size(); i++) {
                        Common.fragmentsList.get(i).showCorrectAnswer();
                        Common.fragmentsList.get(i).disableAnswer();
                    }
                } else if (action.equals("doitagain")) {
                    Log.d("CorrectAnswer", "doitagain");


                    isAnswerModeView = false;
                    countTimer();

                    txt_wrong_answer.setVisibility(View.VISIBLE);
                    txt_right_answer.setVisibility(View.VISIBLE);
                    txt_timer.setVisibility(View.VISIBLE);

                    for (CurrentQuestion item : Common.answerSheetList) {
                        item.setType(Common.ANSWER_TYPE.NO_ANSWER); // RESET ALL QUESTION
                    }
                    Log.d("CorrectAnswer", "RESET ALL QUESTION_Common.ANSWER_TYPE.NO_ANSWER");


                    //answerSheetAdapter = new AnswerSheetAdapter(QuestionActivity.this, Common.answerSheetList);
                    answerSheetAdapter.notifyDataSetChanged();

                    answerSheetHelperAdapter = new AnswerSheetHelperAdapter(this, Common.answerSheetList);
                    answerSheetHelperAdapter.notifyDataSetChanged();

                    for (int i = 0; i < Common.fragmentsList.size(); i++) {
                        Common.fragmentsList.get(i).resetQuestion();
                    }
                    viewPager.setCurrentItem(0);
                    Log.d("CorrectAnswer", "RESET ALL QUESTION.resetQuestion()");
                }
            }
        }

    }

    private void finishGame() {
        int position = viewPager.getCurrentItem();


        if(Common.answerSheetList.get(position)==null ||
                Common.answerSheetList.get(position).getType()== Common.ANSWER_TYPE.NO_ANSWER){

            QuestionFragment questionFragment = Common.fragmentsList.get(position);
            CurrentQuestion question_state = questionFragment.getSelectedAnswer();
            Common.answerSheetList.set(position, question_state); // Set question answer for answersheet
        }



        //For showing correct answer call method here


        answerSheetAdapter.notifyDataSetChanged(); // Change color in answerSheet

        countCorrectAnswer();
        txt_right_answer.setText(new StringBuilder(String.format("%d", Common.right_answer_count))
                .append("/")
                .append(String.format("%d", Common.questionList.size())).toString());

        txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

/*
        if (question_state.getType() == Common.ANSWER_TYPE.NO_ANSWER) {
            questionFragment.showCorrectAnswer();
            questionFragment.disableAnswer();
        }
 */

        for (QuestionFragment fQuestionFragment : Common.fragmentsList) {
            if (fQuestionFragment.getSelectedAnswer().getType() == Common.ANSWER_TYPE.NO_ANSWER) {
                fQuestionFragment.showCorrectAnswer();
                fQuestionFragment.disableAnswer();
            }
        }


        // Here we navigate to result Activity
        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        Common.timer = Common.TOTAL_TIME - time_play;
        Common.no_answer_counter = Common.questionList.size() -
                (Common.wrong_answer_count + Common.right_answer_count);
        Common.data_question = new StringBuilder(new Gson().toJson(Common.answerSheetList));

        startActivityForResult(intent, CODE_GET_RESULT);

    }

}
