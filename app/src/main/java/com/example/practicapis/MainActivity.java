package com.example.practicapis;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.practicapis.nota.DataBase;
import com.example.practicapis.nota.NotaActivity;
import com.example.practicapis.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MenuItem switchArchive;
    private ArrayList<Note> notesList;
    private ArrayList<Note> archivedNotesList;
    private RecyclerView mRecyclerViewNotes;
    private NotesAdapter notesAdapter;
    private AppStatus appStatus;
    private TextView username;
    private FloatingActionButton addNotebtn;
    private MainActivityViewModel viewModel;
    private Context parentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //recyclerList = new ArrayList<>();
        mRecyclerViewNotes = findViewById(R.id.recyclerView);
        mRecyclerViewNotes.setLayoutManager(new GridLayoutManager(this, 2));
        username = findViewById(R.id.userName);
        appStatus = AppStatus.getInstance();
        notesAdapter = new NotesAdapter(this, appStatus.getAllNotes());
        mRecyclerViewNotes.setAdapter(notesAdapter);
        addNotebtn = findViewById(R.id.addNoteBtn);
        parentContext = this.getBaseContext();

        setLiveDataObservers();

        addNotebtn.setOnClickListener(v -> addNote());

        notesList = appStatus.getAllNotes();
        archivedNotesList = appStatus.getArchivedNotes();
        notesAdapter.setLocalDataSet(notesList);
        mRecyclerViewNotes.setAdapter(notesAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        appStatus.setAllNotes(viewModel.getNotes().getValue());
        appStatus.setArchivedNotes(viewModel.getArchivedNotes().getValue());
        notesList = appStatus.getAllNotes();
        archivedNotesList = appStatus.getArchivedNotes();
        if(appStatus.isArchivedView()){
            notesAdapter.setLocalDataSet(appStatus.getArchivedNotes());
        }else{
            notesAdapter.setLocalDataSet(appStatus.getAllNotes());
        }
        mRecyclerViewNotes.setAdapter(notesAdapter);
    }

    public void setLiveDataObservers() {
        // Subscribe the activity to the observable
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        final Observer<ArrayList<Note>> observer = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> arrayList) {
                NotesAdapter newAdapter = new NotesAdapter(parentContext, arrayList);
                mRecyclerViewNotes.swapAdapter(newAdapter, false);
                appStatus.setAllNotes(viewModel.getNotes().getValue());
                appStatus.setArchivedNotes(viewModel.getArchivedNotes().getValue());
                notesList = appStatus.getAllNotes();
                archivedNotesList = appStatus.getArchivedNotes();
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };


        viewModel.getNotes().observe(this, observer);
        //viewModel.getToast().observe(this, observerToast);
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
        final Switch swch = (Switch) menu.findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.action_switch);

        swch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notesAdapter.setLocalDataSet(appStatus.getArchivedNotes());
                    appStatus.setArchivedView();
                } else {
                    notesAdapter.setLocalDataSet(appStatus.getAllNotes());
                    appStatus.setNotesView();
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
        // TODO: Sign out.
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}