package com.example.practicapis.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.practicapis.R;
import com.example.practicapis.localLogic.AppStatus;
import com.example.practicapis.viewModel.DrawingActivityViewModel;

public class DrawingActivity extends AppCompatActivity implements OnClickListener{

    private DrawView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private DrawingActivityViewModel viewModel;
    private String drawingId;
    private AppStatus appStatus;
    private LinearLayout drawingLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        drawingLayout = findViewById(R.id.drawing_layout);
        viewModel = new DrawingActivityViewModel();
        appStatus = AppStatus.getInstance();
        drawView = findViewById(R.id.drawing);
        // Retrieve linear layout paint color is contained
        // Get inital color button
        currPaint = findViewById(R.id.initial_color);
        currPaint.setImageResource(R.drawable.paint_pressed);
        // Get choose brush size button
        drawBtn = findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(drawBtn.getId(), drawBtn);
                return true;
            }
        });
        // Get erase button
        eraseBtn = findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        eraseBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(eraseBtn.getId(), eraseBtn);
                return true;
            }
        });
        // Set new canvas button
        newBtn = findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        newBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(newBtn.getId(), newBtn);
                return true;
            }
        });
        // Save painting button
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        saveBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBtnInfo(saveBtn.getId(), saveBtn);
                return true;
            }
        });
        // Instantiate brushes size
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        // Initiate with medium brush
        drawView.setBrushSize(mediumBrush);

        getDataFromNoteActivity();
        if(drawingId != null){
            viewModel.recoverDrawing(drawingId);
        }
        setLiveDataObservers();
    }

    private void setLiveDataObservers(){
        final Observer<Bitmap> observerDrawingBitmap = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                Log.d("bitmapDA", bitmap.toString());
                drawView.setCanvasBitmap(viewModel.getBitmap().getValue(), drawingLayout);
                paintClicked(findViewById(R.id.black_color));
                paintClicked(findViewById(R.id.initial_color));
            }
        };
        viewModel.getBitmap().observe(this, observerDrawingBitmap);
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
            drawView.setErase(false);
            drawView.setBrushSize(drawView.getLastBrushSize());
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.draw_btn){
            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();

        }else if(view.getId()==R.id.erase_btn){
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }else if(view.getId()==R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    if(!appStatus.isArchivedView()){
                        drawView.setDrawingCacheEnabled(true);
                        drawView.buildDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = drawView.getDrawingCache();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        if(drawingId == null){
                            drawingId = UUID.randomUUID().toString();
                        }
                        viewModel.saveToDb(baos, drawingId);
                        Intent intent = new Intent();
                        intent.putExtra("drawingId", drawingId);
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    private void showBtnInfo(int btnId, ImageButton btn){
        Toast toast = null;
        switch(btnId){
            case R.id.new_btn:
                toast = Toast.makeText(this, "Create blank canvas", Toast.LENGTH_SHORT);
                break;
            case R.id.draw_btn:
                toast = Toast.makeText(this, "Choose brush size", Toast.LENGTH_SHORT);
                break;
            case R.id.erase_btn:
                toast = Toast.makeText(this, "Erase and choose eraser size", Toast.LENGTH_SHORT);
                break;
            case R.id.save_btn:
                toast = Toast.makeText(this, "Save painting", Toast.LENGTH_SHORT);
                break;
        }
        toast.setGravity(Gravity.TOP,0,btn.getHeight() + 200);
        toast.show();
    }

    private void getDataFromNoteActivity(){
        Bundle bundle = getIntent().getExtras();
        String drawingIdFromNote = bundle.getString("drawingId");
        if(drawingIdFromNote != null){
            drawingId = drawingIdFromNote;
        }
    }
}
