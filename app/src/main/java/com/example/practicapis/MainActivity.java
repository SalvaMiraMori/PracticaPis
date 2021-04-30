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
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
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
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        username = findViewById(R.id.userName);
        appStatus = AppStatus.getInstance();
        adapter = new CustomAdapter(this, appStatus.getAllNotes());
        mRecyclerView.setAdapter(adapter);
        addNotebtn = findViewById(R.id.addNoteBtn);
        parentContext = this.getBaseContext();

        setLiveDataObservers();


        addNotebtn.setOnClickListener(v -> addNote());

        appStatus = AppStatus.getInstance();

        notesList = appStatus.getAllNotes();
        adapter.setLocalDataSet(notesList);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appStatus.setAllNotes(viewModel.getNotes().getValue());
        notesList = appStatus.getAllNotes();
    }

    public void setLiveDataObservers() {
        // Subscribe the activity to the observable
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        final Observer<ArrayList<Note>> observer = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> arrayList) {
                CustomAdapter newAdapter = new CustomAdapter(parentContext, arrayList);
                mRecyclerView.swapAdapter(newAdapter, false);
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
                    adapter.setLocalDataSet(appStatus.getArchivedNotes());
                    //adapter.setArchive(true);
                } else {
                    adapter.setLocalDataSet(appStatus.getAllNotes());
                    //adapter.setArchive(false);
                }
                mRecyclerView.setAdapter(adapter);
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

    public void getFromNotaActivity(){
        System.out.println("Bundle de Notas");
        Bundle bundle = getIntent().getExtras();
        Note note = bundle.getParcelable("note");
        int pos = (int)bundle.get("position");

        if((boolean)bundle.get("delete")){                      //Si está marcat per eliminar
            if((int)bundle.get("position") != -1){              //I és una nota editada
                System.out.println("delete");                   //S'ha d'eliminar
                if ((boolean)bundle.get("prevArchive"))
                    appStatus.unarchiveNote(pos);               //Del arxiu
                else
                    appStatus.deleteNote(pos);                  //O de les notes
            }
        }else if(pos == -1){                                    //Si és una nota nova
            System.out.println("add");
            appStatus.addNote(note);                            //S'afegeix a les notes
        }else{
            System.out.println("edit");                         //Si no és nova
            if ((boolean)bundle.get("prevArchive"))             //S'edita
                appStatus.editArchiveNote(note, pos);           //De l'arxiu
            else
                appStatus.editNote(note, pos);                  //O de les notes
        }

        if ((boolean)bundle.get("archive")) {                   //Si està marcada l'opció d'arvivar
            System.out.println("to archive");
            appStatus.archiveNote(note);                        //Es guarda a l'arxiu
        } else {
            System.out.println("no to archive");                //Sinó
            if ((boolean)bundle.get("prevArchive")) {           //I a més estava a l'arxiu
                System.out.println("unarchive");
                appStatus.unarchiveNote(pos);                   //S'elimina de l'arxiu
                appStatus.addNote(note);                        //I es guarda a notes
            }
        }

        note.setFavorite((boolean)bundle.get("favorite"));      //Es marca com a favorit si és necesari

        adapter.setLocalDataSet(appStatus.getAllNotes());
        adapter.setArchive(false);
        mRecyclerView.setAdapter(adapter);
    }

    void signOut(){
        // TODO: Sign out.
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}