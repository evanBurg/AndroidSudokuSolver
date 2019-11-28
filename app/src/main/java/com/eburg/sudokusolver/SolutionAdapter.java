package com.eburg.sudokusolver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {
    private Activity activity;
    ArrayList<Solution> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
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

        holder.providedImage.setImageBitmap(SubjectValues.get(position).getImage());
        holder.date.setText(SubjectValues.get(position).getDate());

        final int pos = position;
        holder.delBtn.setOnClickListener(v -> db.deleteSolution(SubjectValues.get(pos).getId()));
    }

    @Override
    public int getItemCount() {

        return SubjectValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView providedImage;
        private ImageButton delBtn;
        private TextView date;

        public ViewHolder(View v) {
            super(v);

            providedImage = v.findViewById(R.id.providedImage);
            delBtn = v.findViewById(R.id.delete);
            date = v.findViewById(R.id.date);
        }
    }
}
