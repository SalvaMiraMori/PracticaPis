package com.example.practicapis.nota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import androidx.lifecycle.ViewModelProvider;

import com.example.practicapis.MainActivity;
import com.example.practicapis.MainActivityViewModel;
import com.example.practicapis.Note;
import com.example.practicapis.NoteActivityViewModel;
import com.example.practicapis.R;

public class NotaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText title, text;
    private int position;
    private NoteActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
        toolbar = findViewById(R.id.toolbarNote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");

        title = findViewById(R.id.noteTitle);
        text = findViewById(R.id.noteBody);
        viewModel = new ViewModelProvider(this).get(NoteActivityViewModel.class);

        getNoteDataBundle();

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    getSupportActionBar().setTitle(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save){
            if(title.getText().length() != 0){
                onSavePressed();
            } else{
                title.setError("Title can't be blank");
            }

        } else if(item.getItemId() == R.id.delete) {
            onDeletePressed();
        } else {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onSavePressed(){
        /*Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("position", position);
        intent.putExtra("delete", false);
        startActivity(intent);*/

        Note note = new Note(title.getText().toString(), text.getText().toString());
        viewModel.addNote(note);
        //onBackPressed();
        this.finish();
    }

    public void onDeletePressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("delete", true);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void getNoteDataBundle(){
        Bundle bundle = getIntent().getExtras();
        title.setText(bundle.getString("title"));
        text.setText(bundle.getString("body"));
        position = bundle.getInt("position");
    }
}