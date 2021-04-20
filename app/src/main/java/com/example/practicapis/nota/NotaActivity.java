package com.example.practicapis.nota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.practicapis.MainActivity;
import com.example.practicapis.NoteThumbnail;
import com.example.practicapis.R;

public class NotaActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText title, text;
    NoteThumbnail noteThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);
        toolbar = findViewById(R.id.toolbarNote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Note");

        title = findViewById(R.id.noteTitle);
        text = findViewById(R.id.noteBody);

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
                System.out.println("Texto de nota actual: "+text.getText().toString());
                onBackPressed();
            } else{
                title.setError("Title can't be blank");
            }

        } else if(item.getItemId() == R.id.delete) {
            delete();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("noteTitle",title.getText().toString());
        intent.putExtra("noteBody",text.getText().toString());
        intent.putExtra("delete", false);
        startActivity(intent);
    }

    public void delete(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("delete", true);

        startActivity(intent);
    }
}