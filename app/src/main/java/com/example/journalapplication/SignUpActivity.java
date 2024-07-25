package com.example.journalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    //Views
    private EditText mailEdt,userEdt,passEdt;
    Button btnCreate,btnLogin;

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    //FireBaseConnection
    private final FirebaseFirestore firestore= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mailEdt = findViewById(R.id.idEmailSignUp);
        passEdt = findViewById(R.id.idPasswordSignUp);
        userEdt = findViewById(R.id.idUsername);
        btnCreate = findViewById(R.id.idCreateAccountSignup);
        btnLogin = findViewById(R.id.idLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null){
                    //user already logged in
                }
                else{
                    //user not logged in
                }
            }
        };
        
        btnCreate.setOnClickListener(v -> {
            String mail = mailEdt.getText().toString().trim();
            String pass = passEdt.getText().toString().trim();
            String userName = userEdt.getText().toString().trim();
            createUser(mail,pass,userName);
        });
        btnLogin.setOnClickListener(v->{
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        });
    }
    private void createUser(String email,String password,String username){
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(username)){
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "User Not Created", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}