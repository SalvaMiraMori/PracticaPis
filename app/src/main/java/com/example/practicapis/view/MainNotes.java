package com.example.practicapis.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.practicapis.R;
import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.localLogic.Note;
import com.example.practicapis.localLogic.NotesAdapter;
import com.example.practicapis.viewModel.MainNotesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainNotes extends Fragment {
    private RecyclerView mRecyclerViewNotes;
    private NotesAdapter notesAdapter;
    private AppStatus appStatus;

    private MainNotesViewModel viewModel;

    public static MainNotes newInstance() {
        return new MainNotes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_notes_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appStatus = AppStatus.getInstance();
        // Configure recycler view
        mRecyclerViewNotes = getView().findViewById(R.id.recyclerViewNotes);
        //mRecyclerViewNotes.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter(getActivity(), appStatus.getAllNotes());
        mRecyclerViewNotes.setAdapter(notesAdapter);

        notesAdapter.setLocalNoteSet(appStatus.getAllNotes());
        mRecyclerViewNotes.setAdapter(notesAdapter);

        viewModel = new ViewModelProvider(this).get(MainNotesViewModel.class);
        // TODO: Use the ViewModel
        final Observer<ArrayList<Note>> observerNotes = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> arrayList) {
                NotesAdapter newAdapter = new NotesAdapter(getActivity(), arrayList);
                mRecyclerViewNotes.swapAdapter(newAdapter, false);
                appStatus.setAllNotes(viewModel.getNotes().getValue());
                newAdapter.notifyDataSetChanged();
            }
        };
        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(getContext(), t, Toast.LENGTH_SHORT).show();
            }
        };

        viewModel.getNotes().observe(getViewLifecycleOwner(), observerNotes);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        FloatingActionButton filterTagBtn = getActivity().findViewById(R.id.filterTagNotesBtn);
        filterTagBtn.setOnClickListener(v -> filterTag());
    }

    @Override
    public void onResume() {
        super.onResume();
        appStatus.setAllNotes(viewModel.getNotes().getValue());
        notesAdapter.setLocalNoteSet(appStatus.getAllNotes());
        mRecyclerViewNotes.setAdapter(notesAdapter);
    }



    private void filterTag(){
        AlertDialog.Builder filterTagDialog = new AlertDialog.Builder(this.getContext());
        filterTagDialog.setTitle("Filter notes by tag");
        //addTagDialog.setMessage("Add a tag to your note");

        final EditText input = new EditText(this.getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        filterTagDialog.setView(input);

        filterTagDialog.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tagFilter = input.getText().toString();
                ArrayList<Note> filteredNotes = new ArrayList<>();

                for(Note note : appStatus.getAllNotes()){
                    if(note.containsTag(tagFilter)){
                        filteredNotes.add(note);
                        Log.d("MainNotes", note.getTitle());
                    }
                }

                notesAdapter.setLocalNoteSet(filteredNotes);
                mRecyclerViewNotes.setAdapter(notesAdapter);
            }
        });
        filterTagDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        filterTagDialog.setNeutralButton("Delete filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notesAdapter.setLocalNoteSet(appStatus.getAllNotes());
                mRecyclerViewNotes.setAdapter(notesAdapter);
            }
        });

        filterTagDialog.show();
    }
}