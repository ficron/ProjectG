package ua.pp.leonets.quiztest.dbhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ua.pp.leonets.quiztest.interfaces.MyCategoriesCallback;
import ua.pp.leonets.quiztest.interfaces.MyQuestionListCallback;
import ua.pp.leonets.quiztest.model.Category;
import ua.pp.leonets.quiztest.model.Question;

public class OnlineDBHelper {

    private FirebaseDatabase firebaseDatabase;
    private Context context;
    DatabaseReference reference;

    private static OnlineDBHelper instance;


    public OnlineDBHelper(Context context, FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
        this.context = context;
        reference = this.firebaseDatabase.getReference("EDMTQuiz");
    }


    public static synchronized OnlineDBHelper getInstance(Context context, FirebaseDatabase firebaseDatabase) {
        if (instance == null) {
            instance = new OnlineDBHelper(context,firebaseDatabase);
            FirebaseApp.initializeApp(context);
        }else{
            instance.context=context;
        }
        return instance;
    }



    public void getAllCategories(final MyCategoriesCallback myCallback){

        /*

         */
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Category> categoryArrayList = new ArrayList<>();
                        for (DataSnapshot questionSnapshot:dataSnapshot.getChildren()){
                            Category category = new Category();
                            category.setName(questionSnapshot.getKey());
                            categoryArrayList.add(category);
                        }
                        myCallback.setCategoriesList(categoryArrayList);


                        //if (dialog.isShowing())dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void readData (final MyQuestionListCallback myCallback, String category){

         final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setCancelable(false)
                .build();

        if (!dialog.isShowing()){
            dialog.show();
        }

        reference.child(category)
                .child("question")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Question> questionList = new ArrayList<>();
                        for (DataSnapshot questionSnapshot:dataSnapshot.getChildren()){
                            questionList.add(questionSnapshot.getValue(Question.class));
                        }
                        myCallback.setQuestionList(questionList);

                        if (dialog.isShowing())dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }


}
