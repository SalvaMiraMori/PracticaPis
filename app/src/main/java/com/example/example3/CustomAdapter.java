package com.example.example3;

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

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final ArrayList<String> localDataSet;
    private final Context parentContext;
    private final playerInterface listener;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final LinearLayout audioLayout;
        private final ImageButton playButton;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = view.findViewById(R.id.textView);
            audioLayout = view.findViewById(R.id.audio_layout);
            playButton = view.findViewById(R.id.play_button);
        }

        public TextView getTextView() {
            return textView;
        }

        public LinearLayout getLayout() {
            return audioLayout;
        }

        public ImageButton getPlayButton() {return playButton;}
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
                .inflate(R.layout.row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    private void playAudio(View view) {
        // Play audio for clicked note
        ViewGroup parentView = (ViewGroup) view.getParent();
        TextView tv = (TextView) parentView.getChildAt(1);
        String noteText = tv.getText().toString();
        Log.d("TextView", "--> " + noteText);
        listener.startPlaying(noteText);

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
        viewHolder.getTextView().setText(localDataSet.get(position));

        ImageButton playButton = viewHolder.getPlayButton();
        playButton.setOnClickListener(this::playAudio);
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
