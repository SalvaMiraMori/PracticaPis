package com.example.practicapis;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<NoteThumbnail> recyclerList;
    RecyclerView mRecyclerView;
    CustomAdapter adapter;
    AppStatus appStatus;
    TextView username;

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

        // TODO Recuperar dades recyclerview de appstatus


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
    public void addNote(View view) {
        goToNotaActivity();

        //recyclerList.add(new NoteThumbnail(title.getText().toString(), text.getText().toString()));
        /*adapter.setLocalDataSet(recyclerList);
        mRecyclerView.setAdapter(adapter);
        recyclerList.add(new NoteThumbnail("aefafaf", "adfafaf"));*/
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
        startActivity(intent);
    }

    public void getFromNotaActivity(){
        Bundle bundle = getIntent().getExtras();
        NoteThumbnail noteThumbnail;

        // TODO Mirarnoslo mas tarde
        if((boolean)bundle.get("delete") == false){
            /*noteThumbnail = new NoteThumbnail(bundle.getString("noteTitle"), bundle.getString("noteBody"));
            recyclerList.add(0, noteThumbnail);*/
            appStatus.addNotaToList(bundle.getString("noteTitle"), bundle.getString("noteBody"));



            Log.d("Title",bundle.getString("noteTitle"));
        }

    }

    // TODO Abrir un NotaActivity en especifico
    public void editNote(View view){
        /*Intent intent = new Intent(this, NotaActivity.class);
        intent.putExtra("noteTitle",title.getText().toString());
        intent.putExtra("noteBody",text.getText().toString());*/
        System.out.println("Desde el MAIN");
    }
}