package com.example.practicapis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.practicapis.nota.DataBase;
import com.example.practicapis.nota.NotaActivity;
import com.example.practicapis.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Note> recyclerList;
    RecyclerView mRecyclerView;
    CustomAdapter adapter;
    AppStatus appStatus;
    TextView username;
    FloatingActionButton addNotebtn;
    boolean start = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new CustomAdapter(this, recyclerList);
        username = findViewById(R.id.userName);
        appStatus = AppStatus.getInstance();
        addNotebtn = findViewById(R.id.addNoteBtn);

        addNotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        if(appStatus.checkStarted()){
            goToLoginActivity();
            appStatus.appStarted();
        }

        try{
            getFromLoginActivity();
        }catch(Exception e){

        }

        try{
            getFromNotaActivity();
            recyclerList = appStatus.getAllNotes();
            System.out.println("RecycleList: "+recyclerList.size());
            adapter.setLocalDataSet(recyclerList);
            mRecyclerView.setAdapter(adapter);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
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
        Bundle bundle = getIntent().getExtras();

        Note note = bundle.getParcelable("note");

        if((boolean)bundle.get("delete")){
            if((int)bundle.get("position") != -1){
                appStatus.deleteNote((int)bundle.get("position"));
            }
        }else if((int)bundle.get("position") == -1){
            appStatus.addNote(note);
        }else{
            appStatus.editNote(note, bundle.getInt("position"));
        }
    }
}