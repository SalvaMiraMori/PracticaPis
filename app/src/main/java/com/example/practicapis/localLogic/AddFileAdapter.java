package com.example.practicapis.localLogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


import com.example.practicapis.R;

import org.jetbrains.annotations.NotNull;
import com.example.practicapis.view.NotaActivity;

import java.util.ArrayList;
import java.util.UUID;


public class AddFileAdapter extends RecyclerView.Adapter<AddFileAdapter.ViewHolder> {

    private ArrayList<Image> fileList;
    private final Context pareContext;
    private Note note;


    public AddFileAdapter(Context context, ArrayList<Image> fileList){
        this.fileList = fileList;
        pareContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItemView);
        }

        public ImageView getImageView(){ return imageView; }
    }
    @NonNull
    @NotNull
    @Override
    public AddFileAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new ViewHolder(view);
    }

    public ArrayList<Image> getFileList(){ return fileList; }
    public void setFileList(ArrayList<Image> fileList){ this.fileList = fileList; }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.getImageView().setImageBitmap(fileList.get(position).getImatge());
    }

    @Override
    public int getItemCount() {
        if(fileList != null){
            return fileList.size();
        }
        return 0;
    }
}
