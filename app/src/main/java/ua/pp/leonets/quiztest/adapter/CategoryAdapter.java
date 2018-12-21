package ua.pp.leonets.quiztest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.pp.leonets.quiztest.QuestionActivity;
import ua.pp.leonets.quiztest.R;
import ua.pp.leonets.quiztest.common.Common;
import ua.pp.leonets.quiztest.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyVievHolder> {

    Context context;
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
    this.context = context;
    this.categories = categories;
    }

    @NonNull
    @Override
    public MyVievHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_category_intem,viewGroup,false);
        return new MyVievHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVievHolder myVievHolder, int i) {
        myVievHolder.txt_category_name.setText(categories.get(i).getName());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class MyVievHolder extends RecyclerView.ViewHolder {
        CardView card_category;
        TextView txt_category_name;

        public MyVievHolder(@NonNull View itemView) {
            super(itemView);
            card_category = (CardView) itemView.findViewById(R.id.card_category);
            txt_category_name = (TextView) itemView.findViewById(R.id.txt_category_name);


            card_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG","Click at category:" + categories.get(getAdapterPosition()).getName());
                    Common.selectedCategory = categories.get(getAdapterPosition());

                    Intent intent = new Intent(context, QuestionActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }


}
