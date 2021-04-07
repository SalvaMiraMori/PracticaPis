package com.example.practicapis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//public class CustomAdapter extends RecyclerView.Adapter<com.example.PracticaPis.CustomAdapter.ViewHolder> {
public class CustomAdapter extends RecyclerView.Adapter<com.example.practicapis.CustomAdapter.ViewHolder> {
    private final ArrayList<String> localDataSet;
    private final Context parentContext;
    private final playerInterface listener;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final LinearLayout noteLayout;
        private final TextView body;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            title = view.findViewById(R.id.noteTitle);
            noteLayout = view.findViewById(R.id.noteLayout);
            body = view.findViewById(R.id.bodyText);
        }

        public TextView getTitleNote() {
            return title;
        }

        public LinearLayout getLayout() {
            return noteLayout;
        }

        public TextView getBodyNote() {return body;}
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomAdapter(Context current, ArrayList<String> dataSet, playerInterface listener) {
        parentContext = current;
        localDataSet = dataSet;
        this.listener = listener;

    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_item, viewGroup, false);

        return new ViewHolder(view);
    }

    public interface playerInterface{
        void startPlaying(String fileName);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        int color = ContextCompat.getColor(parentContext, R.color.note);
        viewHolder.getLayout().setBackgroundColor(color);
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
