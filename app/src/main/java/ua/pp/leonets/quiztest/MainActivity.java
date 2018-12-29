package ua.pp.leonets.quiztest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.paperdb.Paper;
import ua.pp.leonets.quiztest.adapter.CategoryAdapter;
import ua.pp.leonets.quiztest.common.Common;
import ua.pp.leonets.quiztest.common.SpaceDecoration;
import ua.pp.leonets.quiztest.dbhelper.DBHelper;
import ua.pp.leonets.quiztest.dbhelper.OnlineDBHelper;
import ua.pp.leonets.quiztest.interfaces.MyCategoriesCallback;
import ua.pp.leonets.quiztest.model.Category;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recycler_category;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_settings){
            showSettings();
        }
        
        return true;
    }

    private void showSettings() {
        View settings_layout = LayoutInflater.from(this).inflate(R.layout.settings_layout,null);
        final CheckBox ckb_online_mode = (CheckBox) settings_layout.findViewById(R.id.ckb_online_mode);

        // Load data from Paper, if not available just init default false
        ckb_online_mode.setChecked(Paper.book().read(Common.KEY_SAVE_ONLINE_MODE, false));


        //Show Dialog
        new MaterialStyledDialog.Builder(this)
                .setIcon(R.drawable.ic_settings_white_24dp)
                .setTitle("Налаштування")
                .setDescription("Будь-ласка, оберіть дію")
                .setCustomView(settings_layout)
                .setNegativeText("Відмінити")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Підтвердити")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (ckb_online_mode.isChecked()){
                            Common.isOnlineMode = true;
                        }else {
                            Common.isOnlineMode = false;
                        }

                        Paper.book().write(Common.KEY_SAVE_ONLINE_MODE,ckb_online_mode.isChecked());

                    }
                }).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Paper
        Paper.init(this);

        //Get value online mode
        Common.isOnlineMode = Paper.book().read(Common.KEY_SAVE_ONLINE_MODE,false);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Оберіть категорію для тестування");
        setSupportActionBar(toolbar);

        recycler_category = (RecyclerView) findViewById(R.id.recycler_category);
        recycler_category.setHasFixedSize(true);
        recycler_category.setLayoutManager(new GridLayoutManager(this,1));

        /**
         * Get screen height
         * 8 - Max size of item in category
         */
/*
        CategoryAdapter adapter = new CategoryAdapter(
                this,
                DBHelper.getInstance(this).getAllCategories());
        int spaceInPixel = 4;
        recycler_category.addItemDecoration(new SpaceDecoration(spaceInPixel));
        recycler_category.setAdapter(adapter);
 */


 OnlineDBHelper.getInstance(MainActivity.this, FirebaseDatabase.getInstance())
        .getAllCategories(new MyCategoriesCallback() {
            @Override
            public void setCategoriesList(List<Category> categories) {
                CategoryAdapter adapter = new CategoryAdapter(MainActivity.this,categories);
                int spaceInPixel = 4;
                recycler_category.addItemDecoration(new SpaceDecoration(spaceInPixel));
                recycler_category.setAdapter(adapter);
            }
        });







/*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 8;bn
 */




    }
}
