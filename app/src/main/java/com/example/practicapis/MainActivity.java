package com.example.practicapis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicapis.ui.login.LoginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<NoteThumbnail> recyclerList;
    RecyclerView mRecyclerView;
    CustomAdapter adapter;
    AppStatus appStatus;
    TextView username;
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

        /*if(appStatus.checkStarted()){
            goToLoginActivity();
            appStatus.appStarted();
        }
        getFromLoginActivity();*/
        goToLoginActivity();
        getFromLoginActivity();
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
}