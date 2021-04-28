package com.example.practicapis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.practicapis.nota.DataBase;
import com.example.practicapis.nota.NotaActivity;
import com.example.practicapis.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //ArrayList<Note> recyclerList;
    //ArrayList<Note> archiveNotes;
    RecyclerView mRecyclerView;
    CustomAdapter adapter;
    AppStatus appStatus;
    TextView username;
    FloatingActionButton addNotebtn;
    MenuItem switchArchive;
    boolean start = true;

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

        addNotebtn.setOnClickListener(v -> addNote());

        if(appStatus.checkStarted()) {
            goToLoginActivity();
            appStatus.appStarted();
        }

        try{
            getFromLoginActivity();
        }catch(Exception e){

        }

        try{
            getFromNotaActivity();
        }catch(Exception e){

        }
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
                    adapter.setLocalDataSet(appStatus.getArchiveNotes());
                    adapter.setArchive(true);
                } else {
                    adapter.setLocalDataSet(appStatus.getAllNotes());
                    adapter.setArchive(false);
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
            goToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void getFromLoginActivity(){
        Bundle bundle = getIntent().getExtras();
        System.out.println("Bundle de Login: "+bundle.toString());
        if(bundle != null){
            username.setText(bundle.getString("username"));
            Log.d("Name", username.getText().toString());
        }

    }

    public void goToNotaActivity(){
        Intent intent = new Intent(this, NotaActivity.class);
        intent.putExtra("position", -1);
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
}