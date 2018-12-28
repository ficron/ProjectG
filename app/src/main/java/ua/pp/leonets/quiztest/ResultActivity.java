package ua.pp.leonets.quiztest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.concurrent.TimeUnit;

import ua.pp.leonets.quiztest.adapter.ResultGridAdapter;
import ua.pp.leonets.quiztest.common.Common;
import ua.pp.leonets.quiztest.common.SpaceDecoration;

public class ResultActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_timer, txt_result, txt_right_answer;
    Button btn_filter_total, btn_filter_right, btn_filter_wrong, btn_filter_no_answer;
    RecyclerView recycler_resylt;
    ResultGridAdapter adapter, filterAdapter;

    BroadcastReceiver backToQuestion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().toString().equals(Common.KEY_BACK_FROM_RESULT)) {
                int question = intent.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1);
                goBackActivityWithQuestion(question);
            }
        }
    };

    private void goBackActivityWithQuestion(int question) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Common.KEY_BACK_FROM_RESULT, question);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(backToQuestion, new IntentFilter(Common.KEY_BACK_FROM_RESULT));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Результат");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txt_result = (TextView) findViewById(R.id.text_result);
        txt_right_answer = (TextView) findViewById(R.id.txt_righ_answers);
        txt_timer = (TextView) findViewById(R.id.txt_time);

        btn_filter_total = (Button) findViewById(R.id.btn_filter_total);
        btn_filter_right = (Button) findViewById(R.id.btn_filter_right_answer);
        btn_filter_wrong = (Button) findViewById(R.id.btn_filter_wrong_answer);
        btn_filter_no_answer = (Button) findViewById(R.id.btn_filter_no_answer);

        recycler_resylt = (RecyclerView) findViewById(R.id.recycler_result);
        recycler_resylt.setHasFixedSize(true);
        recycler_resylt.setLayoutManager(new GridLayoutManager(this, 3));

        // Set Adapter
        adapter = new ResultGridAdapter(this, Common.answerSheetList);
        recycler_resylt.addItemDecoration(new SpaceDecoration(4));
        recycler_resylt.setAdapter(adapter);

        txt_timer.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Common.timer),
                TimeUnit.MILLISECONDS.toSeconds(Common.timer) - TimeUnit.MILLISECONDS.toMinutes(Common.timer) * 60
                )
        );

        txt_right_answer.setText(new StringBuilder("")
                .append(Common.right_answer_count)
                .append("/").
                        append(Common.questionList.size()));

        btn_filter_total.setText(new StringBuilder("").append(Common.questionList.size()));
        btn_filter_right.setText(new StringBuilder("").append(Common.right_answer_count));
        btn_filter_wrong.setText(new StringBuilder("").append(Common.wrong_answer_count));
        btn_filter_no_answer.setText(new StringBuilder("").append(Common.no_answer_counter));

        //Calculate result
        int persent = (Common.right_answer_count * 100 / Common.questionList.size());

        if (persent > 90) {
            txt_result.setText(persent+"%");
        } else if (persent > 80) {
            txt_result.setText(persent+"%");
        } else if (persent > 70) {
            txt_result.setText(persent+"%");
        } else if (persent > 60) {
            txt_result.setText(persent+"%");
        } else if (persent > 50) {
            txt_result.setText(persent+"%");
        } else {
            txt_result.setText(persent+"%");
        }

        //Event filter
        btn_filter_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter == null) {
                    adapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetList);
                    recycler_resylt.setAdapter(adapter);
                } else {
                    recycler_resylt.setAdapter(adapter);
                }
            }
        });


        btn_filter_no_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.answerSheetListFiltered.clear();
                for (int i = 0; i < Common.answerSheetList.size(); i++) {
                    if (Common.answerSheetList.get(i).getType() == Common.ANSWER_TYPE.NO_ANSWER) {
                        Common.answerSheetListFiltered.add(Common.answerSheetList.get(i));
                    }
                }
                filterAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetListFiltered);
                recycler_resylt.setAdapter(filterAdapter);
            }
        });


        btn_filter_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.answerSheetListFiltered.clear();
                for (int i = 0; i < Common.answerSheetList.size(); i++) {
                    if (Common.answerSheetList.get(i).getType() == Common.ANSWER_TYPE.WRONG_ANSWER) {
                        Common.answerSheetListFiltered.add(Common.answerSheetList.get(i));
                    }
                }
                filterAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetListFiltered);
                recycler_resylt.setAdapter(filterAdapter);
            }
        });

        btn_filter_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.answerSheetListFiltered.clear();
                for (int i = 0; i < Common.answerSheetList.size(); i++) {
                    if (Common.answerSheetList.get(i).getType() == Common.ANSWER_TYPE.RIGHT_ANSWER) {
                        Common.answerSheetListFiltered.add(Common.answerSheetList.get(i));
                    }
                }
                filterAdapter = new ResultGridAdapter(ResultActivity.this, Common.answerSheetListFiltered);
                recycler_resylt.setAdapter(filterAdapter);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_do_quiz_again:
                doQuizAgain();
                break;
            case R.id.menu_view_answer:
                viewAnswer();
                break;
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Delete all activity
                startActivity(intent);
                break;

        }
        return true;
    }

    private void viewAnswer() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("action","viewquizanswer");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void doQuizAgain() {

        new MaterialStyledDialog.Builder(this)
                .setTitle("Пройти тест знову?")
                .setIcon(R.drawable.ic_mood_black_24dp)
                .setDescription("Все текущая информация сотрется")
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
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("action","doitagain");
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }).show();
    }
}
