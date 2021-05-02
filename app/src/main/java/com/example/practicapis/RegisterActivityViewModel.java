package com.example.practicapis;

public class RegisterActivityViewModel {
    private DatabaseAdapter db;

    public RegisterActivityViewModel(){
        db = DatabaseAdapter.databaseAdapter;
    }

    public void signUpUser(String mail, String password){
        db.signUpUser(mail, password);
    }

    public boolean existsEmail(String email) throws InterruptedException {
        return db.existsEmail(email);
    }
}
