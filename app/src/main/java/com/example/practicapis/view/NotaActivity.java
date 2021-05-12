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
import android.view.Gravity;
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

    private ImageButton locationBtn;
    private ImageButton reminderBtn;
    private ImageButton fileBtn;
    private ImageButton drawableBtn;
    private ImageButton tagBtn;
    private Toolbar toolbar;
    private EditText title, text;
    private Note note;
    private NoteActivityViewModel viewModel;
    public static final String TAG = "NotaActivity";
    private boolean isFavorite;
    private AppStatus appStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
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

        initializeButtons();

        getNoteDataBundle();

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(s);
                //Objects.requireNonNull(getSupportActionBar()).
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initializeButtons(){
        locationBtn =findViewById(R.id.addLocationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapa();
            }
        });
        locationBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(locationBtn.getId(), locationBtn);
                return true;
            }
        });

        reminderBtn = findViewById(R.id.addReminderBtn);
        reminderBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(reminderBtn.getId(), reminderBtn);
                return true;
            }
        });

        drawableBtn = findViewById(R.id.drawBtn);
        drawableBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(drawableBtn.getId(), drawableBtn);
                return true;
            }
        });

        fileBtn = findViewById(R.id.addFileBtn);
        fileBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(fileBtn.getId(), fileBtn);
                return true;
            }
        });

        tagBtn = findViewById(R.id.addTagBtn);
        tagBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(tagBtn.getId(), tagBtn);
                return true;
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

        switch(item.getItemId()){
            case R.id.action_share:
                onSharePressed();
                break;
            case R.id.action_favorite:
                onFavoritePressed();
                break;
            case R.id.action_delete:
                onDeletePressed();
                break;
            case R.id.action_archive:
                onArchivePressed();
                break;
            case android.R.id.home:
                onSavePressed();
                break;
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

    private void showBtnInfo(int btnId, ImageButton btn){
        Toast toast = null;
        switch(btnId){
            case R.id.addLocationBtn:
                toast = Toast.makeText(this, "Add location", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                break;
            case R.id.addFileBtn:
                toast = Toast.makeText(this, "Add file", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                break;
            case R.id.addReminderBtn:
                toast = Toast.makeText(this, "Add reminder", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                break;
            case R.id.addTagBtn:
                toast = Toast.makeText(this, "Add tag", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                break;
            case R.id.drawBtn:
                toast = Toast.makeText(this, "Add drawable", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                break;
        }
        toast.show();
    }
}