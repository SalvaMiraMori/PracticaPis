package com.example.practicapis.database;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAdapter extends AppCompatActivity {

    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    public static vmInterface listener;
    public static DatabaseAdapter databaseAdapter;

    public DatabaseAdapter(vmInterface listener){

        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        this.listener = listener;
        user = mAuth.getCurrentUser();
        listenChanges();
    }

    public DatabaseAdapter(){
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public interface vmInterface{
        void setNotes(ArrayList<Note> notes);
        void setArchivedNotes(ArrayList<Note> archivedNotes);
        void setToast(String s);
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

    public boolean existsEmail(String email) throws InterruptedException {
        final boolean[] isInDb = new boolean[1];
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                isInDb[0] = !isNewUser;
            }
        });
        return isInDb[0];
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
        db.collection("users").document(user.getUid()).collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        retrieved_notes.add(note);
                        Log.d(TAG, "Getting documents: " + title + body + datetime.toString());
                    }
                    listener.setNotes(retrieved_notes);
                } else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getArchivedNotes(){
        Log.d(TAG,"updatearchivednotes with user");
        db.collection("users").document(user.getUid()).collection("archivedNotes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        retrieved_notes.add(note);
                        Log.d(TAG, "Getting documents: " + title + body + datetime.toString());
                    }
                    listener.setArchivedNotes(retrieved_notes);
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
        noteDbMap.put("favorite", note.isFavorite());
        if(note.getLocation() != null){
            noteDbMap.put("location", note.getLocation().toString());
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
        if(note.getLocation() != null){
            noteDbMap.put("location", note.getLocation().toString());
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
        noteDbMap.put("favorite", note.isFavorite());
        if(note.getLocation() != null){
            noteDbMap.put("location" ,note.getLocation().toString());
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
}
