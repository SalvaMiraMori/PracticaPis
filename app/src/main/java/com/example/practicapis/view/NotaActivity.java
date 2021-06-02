package com.example.practicapis.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicapis.localLogic.AddFileAdapter;
import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.localLogic.Image;
import com.example.practicapis.localLogic.Note;
import com.example.practicapis.viewModel.NoteActivityViewModel;
import com.example.practicapis.R;
import com.google.android.gms.maps.model.LatLng;


import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import java.time.LocalDateTime;

public class NotaActivity extends AppCompatActivity {

    private ImageButton locationBtn;
    private ImageButton reminderBtn;
    private ImageButton fileBtn;
    private ImageButton drawableBtn;
    private ImageButton tagBtn;
    private LikeButton favBtn;
    private ImageButton addFileBtn;
    private Toolbar toolbar;
    private EditText title, text;
    private Note note;
    private NoteActivityViewModel viewModel;
    public static final String TAG = "NotaActivity";
    private AppStatus appStatus;
    private RecyclerView recyclerViewImages;
    private ArrayList<Image> imageList;
    private Image image;
    private AddFileAdapter addFileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");

        title = findViewById(R.id.noteTitle);
        text = findViewById(R.id.noteBody);

        recyclerViewImages = findViewById(R.id.recyclerViewFiles);

        viewModel = new ViewModelProvider(this).get(NoteActivityViewModel.class);
        appStatus = AppStatus.getInstance();
        if(appStatus.isArchivedView()){
            title.setEnabled(false);
            text.setEnabled(false);
        }

        addFileAdapter = new AddFileAdapter(this, imageList);
        recyclerViewImages.setAdapter(addFileAdapter);

        initializeButtons();

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
        drawableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrawPressed();
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

        favBtn = findViewById(R.id.star_button);
        favBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                onFavoritePressed(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                onFavoritePressed(false);
            }
        });
        favBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(favBtn.getId(), locationBtn);
                return true;
            }
        });
        addFileBtn = findViewById(R.id.addFileBtn);

        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBtnInfo(addFileBtn.getId(), addFileBtn);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); //TODO Especificar que tipo de archivos
                startActivityForResult(intent, 3);
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

    private void onFavoritePressed(Boolean fav) {
        note.setFavorite(fav);
        if(note.isFavorite()){
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
        }else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                note.setDrawingId(data.getStringExtra("drawingId"));
                Log.d(TAG, note.getDrawingId());
            }
        }else if(requestCode == 3){
            if(resultCode == RESULT_OK){
                /*String path = data.getData().getPath();
                text.setText(path);*/
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                    ImageView imageView = new ImageView(NotaActivity.this);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //imageView.setImageBitmap(bitmap);
                    Image image = new Image(bitmap);
                    //addImageView(imageView, 150, 150);
                    imageList.add(image);
                    addFileAdapter.setFileList(imageList);
                    recyclerViewImages.setAdapter(addFileAdapter);
                    note.setFileList(imageList);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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
        if(note.isFavorite()){ favBtn.setLiked(true); }
        if(appStatus.isArchivedView()){ disableButtons(); }
        if(note.getFileList() != null) {
            imageList = note.getFileList();
        }
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
                break;
            case R.id.addFileBtn:
                toast = Toast.makeText(this, "Add file", Toast.LENGTH_SHORT);
                break;
            case R.id.addReminderBtn:
                toast = Toast.makeText(this, "Add reminder", Toast.LENGTH_SHORT);
                break;
            case R.id.addTagBtn:
                toast = Toast.makeText(this, "Add tag", Toast.LENGTH_SHORT);
                break;
            case R.id.drawBtn:
                toast = Toast.makeText(this, "Add drawable", Toast.LENGTH_SHORT);
                break;
            case R.id.star_button:
                toast = Toast.makeText(this, "Set favorite", Toast.LENGTH_SHORT);
                break;
        }
        toast.setGravity(Gravity.TOP,0,btn.getHeight() + 200);
        toast.show();
    }

    private void disableButtons(){
        favBtn.setEnabled(false);
    }

    public void onDrawPressed(){
        Intent intentDraw = new Intent(this, DrawingActivity.class);
        intentDraw.putExtra("drawingId", note.getDrawingId());
        startActivityForResult(intentDraw, 2);
    }

    public void addImageView(ImageView imageView, int width, int height){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, 10, 0, 10);

        imageView.setLayoutParams(layoutParams);

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}