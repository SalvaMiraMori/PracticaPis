package com.example.practicapis.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

//import com.example.practicapis.nota.DataBase;
import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.viewModel.MainActivityViewModel;
import com.example.practicapis.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private AppStatus appStatus;
    private FloatingActionButton addNotebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure activity elements
        appStatus = AppStatus.getInstance();
        addNotebtn = findViewById(R.id.addNoteBtn);

        addNotebtn.setOnClickListener(v -> addNote());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void addNote() {
        goToNotaActivity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem switchArchive = menu.findItem(R.id.app_bar_switch);
        switchArchive.setActionView(R.layout.switch_item);
        Switch archivedNotesSwitch = (Switch) menu.findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.action_switch);

        archivedNotesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                    navController.navigate(R.id.notes_to_archive);
                    appStatus.setArchivedView();
                    addNotebtn.setVisibility(View.INVISIBLE);
                } else {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                    navController.navigate(R.id.archive_to_notes);
                    appStatus.setNotesView();
                    addNotebtn.setVisibility(View.VISIBLE);
                }

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

    @Override
    public void onBackPressed() {
        finish();
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