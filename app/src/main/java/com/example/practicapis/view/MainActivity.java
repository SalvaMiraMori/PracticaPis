package com.example.practicapis.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.practicapis.nota.DataBase;
import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.viewModel.MainActivityViewModel;
import com.example.practicapis.localLogic.Note;
import com.example.practicapis.localLogic.NotesAdapter;
import com.example.practicapis.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MenuItem switchArchive;
    private RecyclerView mRecyclerViewNotes;
    private NotesAdapter notesAdapter;
    private AppStatus appStatus;
    private FloatingActionButton addNotebtn;
    private MainActivityViewModel viewModel;
    private Context parentContext;
    private Switch archivedNotesSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure recycler view
        mRecyclerViewNotes = findViewById(R.id.recyclerView);
        mRecyclerViewNotes.setLayoutManager(new GridLayoutManager(this, 2));

        // Configure activity elements
        appStatus = AppStatus.getInstance();
        notesAdapter = new NotesAdapter(this, appStatus.getAllNotes());
        mRecyclerViewNotes.setAdapter(notesAdapter);
        addNotebtn = findViewById(R.id.addNoteBtn);
        parentContext = this.getBaseContext();

        setLiveDataObservers();

        addNotebtn.setOnClickListener(v -> addNote());
        notesAdapter.setLocalNoteSet(appStatus.getAllNotes());
        mRecyclerViewNotes.setAdapter(notesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appStatus.setAllNotes(viewModel.getNotes().getValue());
        appStatus.setArchivedNotes(viewModel.getArchivedNotes().getValue());
        if(appStatus.isArchivedView()){
            notesAdapter.setLocalNoteSet(appStatus.getArchivedNotes());
        }else{
            notesAdapter.setLocalNoteSet(appStatus.getAllNotes());
        }
        mRecyclerViewNotes.setAdapter(notesAdapter);
    }

    public void setLiveDataObservers() {
        // Subscribe the activity to the observable
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        final Observer<ArrayList<Note>> observerNotes = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> arrayList) {
                NotesAdapter newAdapter = new NotesAdapter(parentContext, arrayList);
                mRecyclerViewNotes.swapAdapter(newAdapter, false);
                appStatus.setArchivedNotes(viewModel.getArchivedNotes().getValue());
                appStatus.setAllNotes(viewModel.getNotes().getValue());
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };

        viewModel.getNotes().observe(this, observerNotes);
    }

    public void addNote() {
        goToNotaActivity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switchArchive = menu.findItem(R.id.app_bar_switch);
        switchArchive.setActionView(R.layout.switch_item);
        archivedNotesSwitch = (Switch) menu.findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.action_switch);

        archivedNotesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appStatus.setArchivedView();
                    notesAdapter.setLocalNoteSet(appStatus.getArchivedNotes());
                    addNotebtn.setEnabled(false);
                } else {
                    appStatus.setNotesView();
                    notesAdapter.setLocalNoteSet(appStatus.getAllNotes());
                    appStatus.setAllNotes(notesAdapter.getLocalNoteSet());
                    addNotebtn.setEnabled(true);
                }
                mRecyclerViewNotes.setAdapter(notesAdapter);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.singOut) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToNotaActivity(){
        Intent intent = new Intent(this, NotaActivity.class);
        startActivity(intent);
    }

    void signOut(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}