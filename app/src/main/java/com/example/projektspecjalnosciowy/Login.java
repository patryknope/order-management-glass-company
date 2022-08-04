package com.example.projektspecjalnosciowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance(); //inicjalizacja autentykacji firebase
        load = new ProgressDialog(this);

        //gdy już jesteśmy zalogowani, to przenosi nas od razu do activity "Home"
        /*if (firebaseAuth!=null){
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }*/

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPwd);
        Button loginButton = findViewById(R.id.loginBtn);
        TextView loginQuestion = findViewById(R.id.loginQuestion);

        loginQuestion.setOnClickListener(view -> {
            Intent intent = new Intent (Login.this, Registration.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                loginEmail.setError("E-mail jest wymagany!");
                return;
            }
            if (TextUtils.isEmpty(password)){
                loginPassword.setError("Hasło jest wymagane!");

            }else{
                load.setMessage("Logowanie w toku");
                load.setCanceledOnTouchOutside(false);
                load.show();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(Login.this,Home.class);
                        startActivity(intent);
                        finish();
                    }else {
                        // login się nie powiedzie, wyświetlamy błąd
                        String err = task.getException().toString();
                        Toast.makeText(Login.this, "Logowanie się nie powiodło. Spróbuj ponownie!\nBłąd:\n" + err, Toast.LENGTH_SHORT).show();
                    }
                    load.dismiss();

                });
            }
        });
    }
}