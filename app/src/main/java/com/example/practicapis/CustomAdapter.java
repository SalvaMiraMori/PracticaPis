package com.example.practicapis;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicapis.nota.NotaActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<com.example.practicapis.CustomAdapter.ViewHolder> {
    private ArrayList<Note> localDataSet;
    private final Context parentContext;
    private boolean archive;
    AppStatus appStatus = AppStatus.getInstance();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, body;
        private final LinearLayout noteLayout;
        private ImageView favorite;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            favorite = view.findViewById(R.id.imageView);
            title = view.findViewById(R.id.NotaNoteTitle);
            noteLayout = view.findViewById(R.id.noteLayout);
            body = view.findViewById(R.id.NotaBodyText);
        }


        public TextView getTitleNote() {
            return title;
        }

        public LinearLayout getLayout() {
            return noteLayout;
        }

        public TextView getBodyNote() {return body;}
        public ImageView getFavorite() {return favorite;}
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
     CustomAdapter(Context current, ArrayList<Note> dataSet) {
        parentContext = current;
        localDataSet = dataSet;
     }
    public void setArchive(boolean archive) {
         this.archive = archive;
        System.out.println("archive " + archive);
     }
    public void setLocalDataSet(ArrayList<Note> dataSet){
        localDataSet = dataSet;
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item, null, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        int color = ContextCompat.getColor(parentContext, R.color.note);
        viewHolder.getLayout().setBackgroundColor(color);
        viewHolder.getTitleNote().setText(localDataSet.get(position).getTitle());
        viewHolder.getBodyNote().setText(localDataSet.get(position).getBody());
        if (localDataSet.get(position).isFavorite())
            viewHolder.getFavorite().setVisibility(View.VISIBLE);
        else
            viewHolder.getFavorite().setVisibility(View.INVISIBLE);

        LinearLayout layout =viewHolder.getLayout();
        layout.setOnClickListener(v -> {
            Note nota;
            System.out.println("onclic archive: " + archive);
            if (archive)
                nota = appStatus.getNoteArchivedByPosition(position);
            else
                nota = appStatus.getNoteByPosition(position);

            Intent intent = new Intent(v.getContext(), NotaActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("title", nota.getTitle());
            intent.putExtra("body", nota.getBody());
            intent.putExtra("favorite", nota.isFavorite());
            intent.putExtra("prevArchive", archive);
            v.getContext().startActivity(intent);
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (localDataSet != null) {
            return localDataSet.size();
        }
        return 0;
    }
}
