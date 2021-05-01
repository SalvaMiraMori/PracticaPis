package com.example.practicapis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.practicapis.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {
    private RegisterActivityViewModel viewModel;
    private EditText emailTxt;
    private EditText passwordTxt;
    private EditText repeatPasswordTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailTxt = findViewById(R.id.emailInput);
        passwordTxt = findViewById(R.id.passwordInput);
        repeatPasswordTxt = findViewById(R.id.repeatPasswordInput);

        viewModel = new RegisterActivityViewModel();

        final Button registerButton = findViewById(R.id.registrationBtn);
        registerButton.setEnabled(false);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordTxt.getText().toString().isEmpty() || emailTxt.getText().toString().isEmpty() || repeatPasswordTxt.getText().toString().isEmpty()){
                    registerButton.setEnabled(false);
                } else {
                    registerButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        passwordTxt.addTextChangedListener(textWatcher);
        emailTxt.addTextChangedListener(textWatcher);
        repeatPasswordTxt.addTextChangedListener(textWatcher);
    }

    private void goToLoginActivity(){
        if(!emailTxt.getText().toString().contains("@")){
            Toast.makeText(RegisterActivity.this, "Wrong email format.", Toast.LENGTH_SHORT).show();
        }else if(!isPasswordSecure(passwordTxt.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Password not secure enough.", Toast.LENGTH_SHORT).show();
        }else if(!passwordTxt.getText().toString().equals(repeatPasswordTxt.getText().toString())){
            Toast.makeText(RegisterActivity.this, "Repeated password wrong.", Toast.LENGTH_SHORT).show();
        }else if(!viewModel.existsEmail(emailTxt.getText().toString())){
            Toast.makeText(RegisterActivity.this, "E-mail already in use.", Toast.LENGTH_SHORT).show();
        }else{
            viewModel.signUpUser(emailTxt.getText().toString(), passwordTxt.getText().toString());
            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    private boolean isPasswordSecure(String password){
        return password.length() > 5;
    }
}