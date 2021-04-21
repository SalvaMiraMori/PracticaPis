package com.example.practicapis;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicapis.ui.login.LoginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Note> recyclerList;
    private RecyclerView mRecyclerView;
    private CustomAdapter adapter;
    private AppStatus appStatus;
    private TextView username;
    private FloatingActionButton addNotebtn;
    private MainActivityViewModel viewModel;
    private Context parentContext;
    private AppCompatActivity mActivity;

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
        parentContext = this.getBaseContext();
        mActivity = this;

        setLiveDataObservers();

        appStatus = AppStatus.getInstance();

        if(appStatus.checkStarted()){
            goToLoginActivity();
            appStatus.appStarted();
        }

        try{
            getFromLoginActivity();
        }catch(Exception e){

        }


        try{ getFromNotaActivity(); }catch(Exception e){}

        //appStatus.setAllNotes(viewModel.getNotes().getValue());
        recyclerList = appStatus.getAllNotes();
        //System.out.println("RecycleList: "+recyclerList.size());
        adapter.setLocalDataSet(recyclerList);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appStatus.setAllNotes(viewModel.getNotes().getValue());
        recyclerList = appStatus.getAllNotes();
    }

    public void setLiveDataObservers() {
        // Subscribe the activity to the observable
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        //viewModel = new MainActivityViewModel();

        final Observer<MutableLiveData<ArrayList<Note>>> observer = new Observer<MutableLiveData<ArrayList<Note>>>() {
            @Override
            public void onChanged(MutableLiveData<ArrayList<Note>> arrayListMutableLiveData) {
                CustomAdapter newAdapter = new CustomAdapter(parentContext, arrayListMutableLiveData);
                mRecyclerView.swapAdapter(newAdapter, false);
                appStatus.setAllNotes(viewModel.getNotes().getValue());
                recyclerList = appStatus.getAllNotes();
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
    public void addNote(View view) {
        recyclerList.add(new NoteThumbnail("Title", "asñdkjvbnaujfdbnvaoisdjnv iw dfiwdnfcwpaiusdavfqnwasud ... "));
        adapter.setLocalDataSet(recyclerList);
        mRecyclerView.setAdapter(adapter);
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
            viewModel.addNote(note);
        }else{
            appStatus.editNote(note, bundle.getInt("position"));
        }
    }
}