package com.example.practicapis.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.practicapis.database.DatabaseAdapter;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityViewModel extends ViewModel {
    private final DatabaseAdapter adapter;

    public LoginActivityViewModel(){
        adapter = new DatabaseAdapter();
    }

    public void setUser(FirebaseUser user){
        adapter.setUser(user);
    }
}
