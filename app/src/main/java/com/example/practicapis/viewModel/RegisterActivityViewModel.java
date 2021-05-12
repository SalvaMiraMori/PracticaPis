package com.example.practicapis.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.example.practicapis.database.DatabaseAdapter;
import com.example.practicapis.database.DatabaseAdapter.RegisterInterface;

public class RegisterActivityViewModel implements RegisterInterface {
    private DatabaseAdapter db;
    private MutableLiveData<Boolean> existsEmail;

    public RegisterActivityViewModel(){
        db = DatabaseAdapter.databaseAdapter;
        db.setRegisterActivityListener(this);
        existsEmail = new MutableLiveData<>();
    }

    public void signUpUser(String mail, String password){
        db.signUpUser(mail, password);
    }

    public void askExistsEmail(String email) throws InterruptedException {
        db.existsEmail(email);
    }

    @Override
    public void onExistsEmailSucceed(boolean exists) {
        existsEmail.setValue(exists);
    }

    public MutableLiveData<Boolean> existsEmail(){
        return existsEmail;
    }
}
