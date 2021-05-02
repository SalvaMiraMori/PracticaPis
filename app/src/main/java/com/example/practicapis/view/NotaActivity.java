package com.example.practicapis.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.lifecycle.ViewModelProvider;

import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.localLogic.Note;
import com.example.practicapis.viewModel.NoteActivityViewModel;
import com.example.practicapis.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import java.time.LocalDateTime;

public class NotaActivity extends AppCompatActivity {

    private ImageButton location;
    private Toolbar toolbar;
    private EditText title, text;
    private Note note;
    private NoteActivityViewModel viewModel;
    public static final String TAG = "NotaActivity";
    private boolean isFavorite;
    private AppStatus appStatus;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
        toolbar = findViewById(R.id.toolbarNote);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");

        isFavorite = false;
        title = findViewById(R.id.noteTitle);
        text = findViewById(R.id.noteBody);

        viewModel = new ViewModelProvider(this).get(NoteActivityViewModel.class);
        appStatus = AppStatus.getInstance();
        if(appStatus.isArchivedView()){
            title.setEnabled(false);
            text.setEnabled(false);
        }

        location=findViewById(R.id.addLocationBtn);

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
                Objects.requireNonNull(getSupportActionBar()).setTitle(s);
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
        if(isFavorite){
            Toast.makeText(this, title.getText().toString() + " is now a favorite.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, title.getText().toString() + " is no longer a favorite.", Toast.LENGTH_SHORT).show();
        }

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
        if (!note.getTitle().isEmpty()) {
            if (note.getId() == null) {
                viewModel.addNote(note);
            } else {
                viewModel.editNote(note);
            }
        }else if(note.getId() != null){
            Toast.makeText(this, "At least put a title to the note please.", Toast.LENGTH_SHORT).show();
            return;
        }
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                note.setLocation(convertStringToLatLng(data.getStringExtra("location")));
                Log.d(TAG, "Location received " + note.getLocation().toString());
            }
        }
    }

    public void onDeletePressed(){
        try{
            if(appStatus.isArchivedView()){
                viewModel.deleteArchivedNote(note);
            }else{
                if(note.getId() != null){
                    viewModel.deleteNote(note);
                }
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

        if(note.getTitle() != null){
            title.setText(note.getTitle());
            Objects.requireNonNull(getSupportActionBar()).setTitle((CharSequence) title.getText().toString());
        }
        if(note.getBody() != null){ text.setText(note.getBody()); }
        isFavorite = note.isFavorite();
    }

    public void goToMapa(){
        Intent intent = new Intent(this, MapsActivity.class);
        if(note.getLocation() != null){
            intent.putExtra("location", note.getLocation());
        }
        startActivityForResult(intent, 1);
    }

    private LatLng convertStringToLatLng(String location){
        String[] latlong =  location.split(",");
        String[] lat = latlong[0].split("\\(");
        String[] lng = latlong[1].split("\\)");

        double latitude = Double.parseDouble(lat[1]);
        double longitude = Double.parseDouble(lng[0]);
        return new LatLng(latitude, longitude);
    }
}