package com.example.practicapis.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.practicapis.R;
import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.localLogic.Note;
import com.example.practicapis.localLogic.NotesAdapter;
import com.example.practicapis.viewModel.MainArchiveViewModel;

import java.util.ArrayList;

public class MainArchive extends Fragment {
    private RecyclerView mRecyclerViewArchive;
    private NotesAdapter archiveAdapter;
    private AppStatus appStatus;
    private MainArchiveViewModel mViewModel;

    public static MainArchive newInstance() {
        return new MainArchive();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_archive_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appStatus = AppStatus.getInstance();
        // Configure recycler view
        mRecyclerViewArchive = getView().findViewById(R.id.recyclerViewArchive);
        mRecyclerViewArchive.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));

        archiveAdapter = new NotesAdapter(getActivity(), appStatus.getArchivedNotes());
        mRecyclerViewArchive.setAdapter(archiveAdapter);

        archiveAdapter.setLocalNoteSet(appStatus.getArchivedNotes());
        mRecyclerViewArchive.setAdapter(archiveAdapter);
        mViewModel = new ViewModelProvider(this).get(MainArchiveViewModel.class);
        // TODO: Use the ViewModel
        final Observer<ArrayList<Note>> observerNotes = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> arrayList) {
                NotesAdapter newAdapter = new NotesAdapter(getActivity(), arrayList);
                mRecyclerViewArchive.swapAdapter(newAdapter, false);
                appStatus.setArchivedNotes(mViewModel.getArchivedNotes().getValue());
                newAdapter.notifyDataSetChanged();
            }
        };
        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(getContext(), t, Toast.LENGTH_SHORT).show();
            }
        };

        mViewModel.getArchivedNotes().observe(getViewLifecycleOwner(), observerNotes);
    }

    @Override
    public void onResume() {
        super.onResume();
        appStatus.setArchivedNotes(mViewModel.getArchivedNotes().getValue());
        archiveAdapter.setLocalNoteSet(appStatus.getArchivedNotes());

        mRecyclerViewArchive.setAdapter(archiveAdapter);
    }
}