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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText mailEdt,passEdt;
    Button btnLogin;

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();


        mailEdt = findViewById(R.id.idEmailLogin);
        passEdt = findViewById(R.id.idPasswordLogin);
        Button btn = findViewById(R.id.idSignUp);
        btnLogin = findViewById(R.id.idSignIn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mailEdt.getText().toString().trim();
                String pass = passEdt.getText().toString().trim();
                loginUser(mail,pass);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(MainActivity.this, JournalListActivity.class));
        }
    }

    private void loginUser(String mail, String pass){
        if(!TextUtils.isEmpty(mail)&&!TextUtils.isEmpty(pass)){
            firebaseAuth.signInWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    currentUser  = firebaseAuth.getCurrentUser();
                    startActivity(new Intent(MainActivity.this, JournalListActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
        else{
            Toast.makeText(this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
        }
    }
}