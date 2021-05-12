package com.example.practicapis.localLogic;

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

import com.example.practicapis.R;
import com.example.practicapis.view.NotaActivity;

import java.util.ArrayList;
import java.util.Collections;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private ArrayList<Note> localNoteSet;
    private final Context parentContext;
    private AppStatus appStatus = AppStatus.getInstance();

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
     public NotesAdapter(Context current, ArrayList<Note> dataSet) {
        parentContext = current;
        localNoteSet = sortByFavourite(dataSet);
     }

    public void setLocalNoteSet(ArrayList<Note> dataSet){
        localNoteSet = dataSet;
        if(localNoteSet != null && !appStatus.isArchivedView()){
            localNoteSet = sortByFavourite(localNoteSet);
        }
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
        viewHolder.getTitleNote().setText(localNoteSet.get(position).getTitle());
        viewHolder.getBodyNote().setText(localNoteSet.get(position).getBody());
        System.out.println("Note " + localNoteSet.get(position).getTitle() + " OnCustomAdapter: isFavorite " + localNoteSet.get(position).isFavorite());
        if (localNoteSet.get(position).isFavorite())
            viewHolder.getFavorite().setVisibility(View.VISIBLE);
        else
            viewHolder.getFavorite().setVisibility(View.INVISIBLE);


        // TODO Ir al notaActivity en concreto
        LinearLayout layout =viewHolder.getLayout();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = null;
                if(appStatus.isArchivedView()){
                    note = appStatus.getNoteArchivedByPosition(position);
                }else{
                    note = appStatus.getNoteByPosition(position);
                }
                Intent intent = new Intent(v.getContext(), NotaActivity.class);
                intent.putExtra("note", note);
                v.getContext().startActivity(intent);
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (localNoteSet != null) {
            return localNoteSet.size();
        }
        return 0;
    }

    private ArrayList<Note> sortByFavourite(ArrayList<Note> notes){
         ArrayList<Note> favourites = new ArrayList<>();
         ArrayList<Note> notFavourites = new ArrayList<>();
         for(Note note : notes){
             if(note.isFavorite()){
                 favourites.add(note);
             }else{
                 notFavourites.add(note);
             }
         }
         Collections.sort(favourites, Collections.reverseOrder());
         Collections.sort(notFavourites, Collections.reverseOrder());
         favourites.addAll(notFavourites);
         return favourites;
    }

    public ArrayList<Note> getLocalNoteSet(){ return localNoteSet; }
}
