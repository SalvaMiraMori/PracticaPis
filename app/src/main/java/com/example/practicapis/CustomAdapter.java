package com.example.practicapis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<com.example.practicapis.CustomAdapter.ViewHolder> {

    private MutableLiveData<ArrayList<Note>> localDataSet;
    private final Context parentContext;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, body;
        private final LinearLayout noteLayout;

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

    public CustomAdapter(Context current, MutableLiveData<ArrayList<Note>> dataSet) {
        parentContext = current;
        localDataSet = dataSet;
    }
    public void setLocalDataSet(ArrayList<Note> dataSet){
        localDataSet.setValue(dataSet);
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
        System.out.println("onBind");
        int color = ContextCompat.getColor(parentContext, R.color.note);
        viewHolder.getLayout().setBackgroundColor(color);
        viewHolder.getTitleNote().setText(localDataSet.getValue().get(position).getTitle());
        viewHolder.getBodyNote().setText(localDataSet.getValue().get(position).getBody());


        // TODO Ir al notaActivity en concreto
        LinearLayout layout =viewHolder.getLayout();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note nota = appStatus.getNoteByPosition(position);
                Intent intent = new Intent(v.getContext(), NotaActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("title", nota.getTitle());
                intent.putExtra("body", nota.getBody());
                v.getContext().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (localDataSet != null) {
            return localDataSet.getValue().size();
        }
        return 0;
    }

}
