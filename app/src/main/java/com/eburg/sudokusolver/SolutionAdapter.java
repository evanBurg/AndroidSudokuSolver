package com.eburg.sudokusolver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.eburg.sudokusolver.SolveActivity.getBorder;


public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {
    ArrayList<Solution> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    private Activity activity;
    private DBAdapter db;

    public SolutionAdapter(Context context1, ArrayList<Solution> SubjectValues1, DBAdapter db, Activity activity) {

        SubjectValues = SubjectValues1;
        context = context1;
        this.db = db;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.solution_layout, parent, false);
        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            holder.providedImage.setImageBitmap(MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), SubjectValues.get(position).getImage()));
        } catch (Exception e) {
            holder.providedImage.setImageResource(R.drawable.ic_history);
        }
        s
        holder.date.setText(SubjectValues.get(position).getDate());

        final int pos = position;
        holder.delBtn.setOnClickListener(v -> db.deleteSolution(SubjectValues.get(pos).getId()));

        holder.cardView.setOnClickListener(v -> toggleSolution(holder, position));
        holder.solveBtn.setOnClickListener(v -> toggleSolution(holder, position));

        ArrayList<ArrayList<Integer>> solutionArray = SubjectValues.get(position).getSolution();
        ArrayList<ArrayList<Integer>> problemArray = SubjectValues.get(position).getSolution();
        for (int i = 0; i < 9; i++) {
            LinearLayout row = new LinearLayout(this.context);
            holder.inputBoard.add(new ArrayList<EditText>());
            for (int j = 0; j < 9; j++) {
                EditText text = new EditText(this.context);
                text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                text.setInputType(InputType.TYPE_CLASS_NUMBER);
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                int maxLength = 1;
                text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                text.setBackgroundResource(getBorder(j, i));
                text.setEnabled(false);
                text.setOnClickListener(v -> toggleSolution(holder, position));

                Integer input = problemArray.get(i).get(j);
                Integer answer = solutionArray.get(i).get(j);

                try {
                    text.setText(String.valueOf(answer));
                    text.setTextColor(Color.BLACK);
                    if (input == 0) {
                        text.setTextColor(Color.LTGRAY);
                    }
                } catch (Exception e) {
                    String what = e.getMessage();
                    String className = e.toString();
                }

                row.addView(text);
                holder.inputBoard.get(i).add(text);
            }
            holder.solutionContainer.addView(row);
        }
    }

    private void toggleSolution(ViewHolder holder, int position) {
        if (holder.showingSolution) {
            holder.showingSolution = false;
            TransitionManager.beginDelayedTransition(holder.cardView, new TransitionSet().addTransition(new ChangeBounds()));
            TransitionManager.beginDelayedTransition(holder.buttonContainer, new TransitionSet().addTransition(new ChangeBounds()));
//            holder.solveBtn.setBackgroundResource(R.drawable.button_shape);
            holder.solveBtn.setImageResource(R.drawable.ic_check_circle_black_24dp);
            holder.providedImage.setVisibility(View.VISIBLE);
            holder.solutionContainer.setVisibility(View.INVISIBLE);
        } else {
            holder.showingSolution = true;
//            holder.solveBtn.setBackgroundResource(R.drawable.button_shape_black);
            holder.solveBtn.setImageResource(R.drawable.ic_check_circle_green_500_24dp);
            holder.providedImage.setVisibility(View.INVISIBLE);
            holder.solutionContainer.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(holder.cardView, new TransitionSet().addTransition(new ChangeBounds()));
            TransitionManager.beginDelayedTransition(holder.buttonContainer, new TransitionSet().addTransition(new ChangeBounds()));
        }
    }

    @Override
    public int getItemCount() {

        return SubjectValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView providedImage;
        private ImageButton delBtn;
        private ImageButton solveBtn;
        private TextView date;
        private CardView cardView;
        private ConstraintLayout buttonContainer;
        private LinearLayout solutionContainer;
        private ArrayList<ArrayList<EditText>> inputBoard;
        private boolean showingSolution = false;

        public ViewHolder(View v) {
            super(v);

            providedImage = v.findViewById(R.id.providedImage);
            delBtn = v.findViewById(R.id.delete);
            solveBtn = v.findViewById(R.id.slvBtn);
            date = v.findViewById(R.id.date);
            cardView = v.findViewById(R.id.card_view);
            solutionContainer = v.findViewById(R.id.solutionContainer);
            buttonContainer = v.findViewById(R.id.buttonContainer);
            inputBoard = new ArrayList<>();
        }
    }
}
