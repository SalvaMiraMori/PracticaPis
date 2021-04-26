package com.example.practicapis;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class DatabaseAdapter {

    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
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
        //initFirebase();
        //listenChanges();
    }

    public void setListener(vmInterface listener){
        this.listener = listener;
    }

    public interface vmInterface{
        void setCollection(ArrayList<Note> ac);
        void setToast(String s);
    }

    public void initFirebase(){

        user = mAuth.getCurrentUser();

        if (user == null) {
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        listener.setToast("Authentication successful.");
                        user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        listener.setToast("Authentication failed.");

                    }
                }
            });
                    /*.addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                listener.setToast("Authentication successful.");
                                user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                listener.setToast("Authentication failed.");

                            }
                        }
                    });*/
        }
        else{
            listener.setToast("Authentication with current user.");

        }
    }

    public void setUser(FirebaseUser user){
        this.user = user;
        /*if(userIsInDb(user.getUid())){

        }*/
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

                    }
                }
            });
            /*db.collection("users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "User added with ID: " + documentReference.getId());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding user", e);
                }
            });*/
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

    private String getUserCollectionId(String uId){
        final String[] isInDb = new String[1];
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d(TAG, "User with ID: " + document.getString("userId"));
                        if(document.getString("userId").equals(uId)){
                            isInDb[0] = document.getId();
                            return;
                        }
                    }
                }
            }
        });

        return isInDb[0];
    }

    public void getCollection(){

        //try{
        Log.d(TAG,"updatenotes with user");
        db.collection("users").document(user.getUid()).collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Note> retrieved_notes = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        retrieved_notes.add(new Note(document.getString("title"), document.getString("body"), document.getId()));
                        Log.d(TAG, "Getting documents: " + document.getString("title") + document.getString("body"));
                    }
                    listener.setCollection(retrieved_notes);
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

    public void deleteNote(Note note){
        db.collection("users").document(user.getUid()).collection("notes").document(note.getId()).delete();
    }

    public void editNote(Note note){
        Map<String, Object> noteDbMap = new HashMap<>();
        noteDbMap.put("title", note.getTitle());
        noteDbMap.put("body", note.getBody());
        db.collection("users").document(user.getUid()).collection("notes").document(note.getId()).update(noteDbMap);
    }

    public void listenChanges(){
        db.collection("users").document(user.getUid()).collection("notes").addSnapshotListener((snapshots, e) -> {
            DatabaseAdapter.this.getCollection();
        });
    }
}
