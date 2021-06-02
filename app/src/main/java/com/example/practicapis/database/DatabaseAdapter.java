package com.example.practicapis.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practicapis.localLogic.Note;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseAdapter extends AppCompatActivity {

    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    public static MainInterface mainActivityListener;
    public static RegisterInterface registerActivityListener;
    public static DrawingInterface drawingInterfaceListener;
    public static DatabaseAdapter databaseAdapter;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    public DatabaseAdapter(MainInterface mainActivityListener){
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        this.mainActivityListener = mainActivityListener;
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://practicapis.appspot.com");
        listenChanges();
    }

    public DatabaseAdapter(){
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://practicapis.appspot.com");
    }

    public interface MainInterface {
        void setNotes(ArrayList<Note> notes);
        void setArchivedNotes(ArrayList<Note> archivedNotes);
        void setToast(String s);
    }

    public interface RegisterInterface {
        void onExistsEmailSucceed(boolean exists);
    }

    public interface DrawingInterface {
        void setDrawingBitmap(Bitmap bitmap);
    }

    public void setRegisterActivityListener(RegisterInterface registerInterface){
        this.registerActivityListener = registerInterface;
    }

    public void setDrawingInterfaceListener(DrawingInterface drawingInterface){
        this.drawingInterfaceListener = drawingInterface;
    }

    public void signUpUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(DatabaseAdapter.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void existsEmail(String email) throws InterruptedException {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                registerActivityListener.onExistsEmailSucceed(!isNewUser);
            }
        });
    }

    public void setUser(FirebaseUser user){
        this.user = user;
        Log.d(TAG, "setUser");
        if(!userIsInDb(user.getUid())){
            Map<String, String> userMap = new HashMap<>();
            userMap.put("userId", user.getUid());

            DocumentReference usersCollection = db.collection("users").document(user.getUid());
            usersCollection.set(userMap);
            usersCollection.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "User added with ID: " + user.getUid());
                    }else{
                        Log.d(TAG, "User not added.");
                    }
                }
            });
        }
    }

    private boolean userIsInDb(String uId){
        final boolean[] isInDb = new boolean[1];
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d(TAG, "User with ID: " + document.getString("userId"));
                        if(document.getString("userId").equals(uId)){
                            isInDb[0] = true;
                            return;
                        }
                    }
                    isInDb[0] = false;
                    return;
                }
            }
        });
        return isInDb[0];
    }

    public void getNotes(){

        //try{
        Log.d(TAG,"updatenotes with user");
        db.collection("users").document(user.getUid()).collection("notes").orderBy("favorite", Query.Direction.DESCENDING).orderBy("serverTime", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Note> retrieved_notes = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String title = document.getString("title");
                        String body = document.getString("body");
                        Boolean favorite = document.getBoolean("favorite");
                        String id = document.getId();
                        String location = document.getString("location");
                        LocalDateTime datetime = LocalDateTime.parse(document.getString("datetime"));

                        Note note = new Note(title, body, id, datetime);
                        note.setFavorite(favorite);
                        if(location != null){
                            note.setLocation(convertStringToLatLng(location));
                        }
                        try{
                            String drawingId = document.getString("drawingId");
                            if(drawingId != null){
                                note.setDrawingId(drawingId);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            ArrayList<String> tags = (ArrayList<String>) document.get("tags");
                            note.setTags(tags);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            String color = document.getString("color");
                            note.setColor(color);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        retrieved_notes.add(note);
                        Log.d(TAG, "Getting documents: " + title + body + datetime.toString());
                    }
                    //Collections.reverse(retrieved_notes);
                    mainActivityListener.setNotes(retrieved_notes);
                } else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getArchivedNotes(){
        Log.d(TAG,"updatearchivednotes with user");
        db.collection("users").document(user.getUid()).collection("archivedNotes").orderBy("serverTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Note> retrieved_notes = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String title = document.getString("title");
                        String body = document.getString("body");
                        String id = document.getId();
                        String location = document.getString("location");
                        LocalDateTime datetime = LocalDateTime.parse(document.getString("datetime"));
                        Note note = new Note(title, body, id, datetime);
                        if(location != null){
                            note.setLocation(convertStringToLatLng(location));
                        }
                        try{
                            String drawingId = document.getString("drawingId");
                            if(drawingId != null){
                                note.setDrawingId(drawingId);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            ArrayList<String> tags = (ArrayList<String>) document.get("tags");
                            note.setTags(tags);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            String color = document.getString("color");
                            note.setColor(color);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        retrieved_notes.add(note);
                        Log.d(TAG, "Getting documents: " + title + body + datetime.toString());
                    }
                    mainActivityListener.setArchivedNotes(retrieved_notes);
                } else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void saveNote(Note note){
        Map<String, Object> noteDbMap = new HashMap<>();
        noteDbMap.put("title", note.getTitle());
        noteDbMap.put("body", note.getBody());
        noteDbMap.put("datetime", note.getDate().toString());
        noteDbMap.put("serverTime", FieldValue.serverTimestamp());
        noteDbMap.put("favorite", note.isFavorite());
        noteDbMap.put("tags", note.getTags());
        noteDbMap.put("color", note.getColor());
        if(note.getLocation() != null){
            noteDbMap.put("location", note.getLocation().toString());
        }
        if(note.getDrawingId() != null){
            noteDbMap.put("drawingId", note.getDrawingId());
        }
        Log.d(TAG, "saveDocument");

        db.collection("users")
                .document(user.getUid())
                .collection("notes")
                .add(noteDbMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }

    public void archiveNote(Note note){
        Map<String, Object> noteDbMap = new HashMap<>();
        noteDbMap.put("title", note.getTitle());
        noteDbMap.put("body", note.getBody());
        noteDbMap.put("datetime", note.getDate().toString());
        noteDbMap.put("serverTime", FieldValue.serverTimestamp());
        noteDbMap.put("tags", note.getTags());
        noteDbMap.put("color", note.getColor());
        if(note.getLocation() != null){
            noteDbMap.put("location", note.getLocation().toString());
        }
        if(note.getDrawingId() != null){
            noteDbMap.put("drawingId", note.getDrawingId());
        }

        Log.d(TAG, "saveDocument");

        db.collection("users")
                .document(user.getUid())
                .collection("archivedNotes")
                .add(noteDbMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });

        if(note.getId() != null){
            deleteNote(note);
        }
    }

    public void deleteNote(Note note){
        db.collection("users").document(user.getUid()).collection("notes").document(note.getId()).delete();
    }

    public void deleteArchivedNote(Note note) {
        db.collection("users").document(user.getUid()).collection("archivedNotes").document(note.getId()).delete();
    }

    public void editNote(Note note){
        Map<String, Object> noteDbMap = new HashMap<>();
        noteDbMap.put("title", note.getTitle());
        noteDbMap.put("body", note.getBody());
        noteDbMap.put("datetime", note.getDate().toString());
        noteDbMap.put("serverTime", FieldValue.serverTimestamp());
        noteDbMap.put("favorite", note.isFavorite());
        noteDbMap.put("tags", note.getTags());
        noteDbMap.put("color", note.getColor());
        if(note.getLocation() != null){
            noteDbMap.put("location" ,note.getLocation().toString());
        }
        if(note.getDrawingId() != null){
            noteDbMap.put("drawingId", note.getDrawingId());
        }

        db.collection("users").document(user.getUid()).collection("notes").document(note.getId()).update(noteDbMap);
    }

    public void listenChanges(){
        db.collection("users").document(user.getUid()).collection("notes").addSnapshotListener((snapshots, e) -> {
            DatabaseAdapter.this.getNotes();
        });
        db.collection("users").document(user.getUid()).collection("archivedNotes").addSnapshotListener((snapshots, e) -> {
            DatabaseAdapter.this.getArchivedNotes();
        });
    }

    private LatLng convertStringToLatLng(String location){
        String[] latlong =  location.split(",");
        String[] lat = latlong[0].split("\\(");
        String[] lng = latlong[1].split("\\)");

        double latitude = Double.parseDouble(lat[1]);
        double longitude = Double.parseDouble(lng[0]);
        return new LatLng(latitude, longitude);
    }

    public void saveDrawing(ByteArrayOutputStream baos, String drawingId){
        byte[] data = baos.toByteArray();

        drawingId = drawingId + ".png";

        StorageReference drawingRef = storageRef.child("drawings/" + drawingId);

        UploadTask uploadTask = drawingRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    public void recoverDrawing(String drawingId){
        StorageReference drawingRef = storageRef.child("drawings/" + drawingId + ".png");
        drawingRef.getBytes(2048 * 2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                drawingInterfaceListener.setDrawingBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                Log.d(TAG, "drawing recovered");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "failed drawing recovery");
            }
        });
    }

    public void saveImages(ByteArrayOutputStream baos, String imageId){
        /*byte[] data = baos.toByteArray();

        imageId = imageId + ".png";
        StorageReference imageRef = storageRef.child("images/" + imageId);

        UploadTask uploadTask = imageRef.putBytes()*/
    }
}
