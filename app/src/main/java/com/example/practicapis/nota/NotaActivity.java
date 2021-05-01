package com.example.practicapis.nota;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import androidx.lifecycle.ViewModelProvider;

import com.example.practicapis.AppStatus;
import com.example.practicapis.MainActivity;
import com.example.practicapis.MapsActivity;
import com.example.practicapis.MainActivityViewModel;
import com.example.practicapis.Note;
import com.example.practicapis.NoteActivityViewModel;
import com.example.practicapis.R;

import java.util.Objects;

import java.time.LocalDateTime;

public class NotaActivity extends AppCompatActivity {

    private ImageButton location;
    private Toolbar toolbar;
    private EditText title, text;
    private Note note;
    private NoteActivityViewModel viewModel;
    public static final String TAG = "NotaActivity";
    private boolean isFavorite, toArchive, prevArchive;
    private AppStatus appStatus;

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

        viewModel = new ViewModelProvider(this).get(NoteActivityViewModel.class);
        appStatus = AppStatus.getInstance();
        if(appStatus.isArchivedView()){
            title.setEnabled(false);
            text.setEnabled(false);
        }

        location=findViewById(R.id.mapa_btn);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapa();
            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onArchivePressed() {
        note.setTitle(title.getText().toString());
        note.setBody(text.getText().toString());
        if(note.getDate() == null){
            note.setDate(LocalDateTime.now());
        }
        viewModel.archiveNote(note);
        onBackPressed();
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
        note.setFavorite(!note.isFavorite());
        isFavorite = note.isFavorite();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSavePressed(){
        // TODO: Guardar preferits i archivats.
        if(appStatus.isArchivedView()){
            onBackPressed();
        }
        note.setTitle(title.getText().toString());
        note.setBody(text.getText().toString());
        note.setDate(LocalDateTime.now());
        note.setFavorite(isFavorite);
        Log.d(TAG, "Local time " + LocalDateTime.now().toString());
        if(note.getId() == null){
            viewModel.addNote(note);
        }else{
            viewModel.editNote(note);
        }
        if (note.getTitle().isEmpty() && note.getBody().isEmpty())
            viewModel.deleteNote(note);
        onBackPressed();
    }

    public void onDeletePressed(){
        try{
            if(appStatus.isArchivedView()){
                viewModel.deleteArchivedNote(note);
            }else{
                viewModel.deleteNote(note);
            }
        }catch(Exception e){ }
        this.finish();
    }

    public void getNoteDataBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            note = (Note) bundle.get("note");
        }else{
            note = new Note();
        }

        if(note.getTitle() != null){ title.setText(note.getTitle()); }
        if(note.getBody() != null){ text.setText(note.getBody()); }
        isFavorite = note.isFavorite();
    }

    public void goToMapa(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}