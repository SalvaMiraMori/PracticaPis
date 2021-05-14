package com.example.practicapis.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.practicapis.R;

public class DrawingActivity extends AppCompatActivity {

    private DrawView drawView;
    private ImageButton currPaint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        drawView = findViewById(R.id.drawing);
        // Retrieve linear layout paint color is contained
        // Get first button
        currPaint = findViewById(R.id.initial_color);
        currPaint.setImageResource(R.drawable.paint_pressed);
    }

    public void paintClicked(View view){
        if(view != currPaint){
            // Update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageResource(R.drawable.paint_pressed);
            currPaint.setImageResource(R.drawable.paint);
            currPaint=(ImageButton)view;
        }
    }
}
