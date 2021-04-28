package com.example.practicapis.nota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.example.practicapis.MainActivity;
import com.example.practicapis.Note;
import com.example.practicapis.R;

import java.util.Objects;

public class NotaActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText title, text;
    Note note;
    int position;
    boolean isFavorite, toArchive, prevArchive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
        toolbar = findViewById(R.id.toolbarNote);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");

        isFavorite = false;
        toArchive = false;
        title = findViewById(R.id.noteTitle);
        text = findViewById(R.id.noteBody);
        getNoteDataBundle();

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    Objects.requireNonNull(getSupportActionBar()).setTitle(s);
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
        inflater.inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            onSharePressed();
        } else if(item.getItemId() == R.id.action_favorite){
            onFavoritePressed();
        } else if(item.getItemId() == R.id.action_delete) {
            onDeletePressed();
        } else if (item.getItemId() == R.id.action_archive){
            onArchivePressed();
        } else {
            onSavePressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onArchivePressed() {
        toArchive = !toArchive;
        System.out.println("archive: "+toArchive);
    }

    private void onSharePressed() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String toShare = title.getText().toString()+"\n"+text.getText().toString();
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, toShare);
        //TODO put maps and files of the note
        startActivity(Intent.createChooser(shareIntent, "Share using..."));
    }

    private void onFavoritePressed() {
        isFavorite = !isFavorite;
        System.out.println("favorite: "+isFavorite);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onSavePressed(){
        Intent intent = new Intent(this, MainActivity.class);
        Note note = new Note(title.getText().toString(), text.getText().toString());
        intent.putExtra("note", note);
        intent.putExtra("position", position);
        intent.putExtra("delete", false);
        intent.putExtra("favorite", isFavorite);
        intent.putExtra("archive", toArchive);
        intent.putExtra("prevArchive", prevArchive);
        startActivity(intent);
    }

    public void onDeletePressed(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("delete", true);
        intent.putExtra("position", position);
        intent.putExtra("prevArchive", prevArchive);
        startActivity(intent);
    }

    public void getNoteDataBundle(){
        Bundle bundle = getIntent().getExtras();
        title.setText(bundle.getString("title"));
        text.setText(bundle.getString("body"));
        position = bundle.getInt("position");
        isFavorite = bundle.getBoolean("favorite");
        prevArchive = bundle.getBoolean("prevArchive");
    }
}