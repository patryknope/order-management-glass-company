package com.example.projektspecjalnosciowy;


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

import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText registrationEmail;
    private EditText registrationPassword;
    private Button registrationButton;
    private TextView registrationQuestion;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();
        load = new ProgressDialog(this);


        registrationEmail = findViewById(R.id.regEmail);
        registrationPassword = findViewById(R.id.regPwd);
        registrationButton = findViewById(R.id.regBtn);
        registrationQuestion = findViewById(R.id.regQuestion);

        registrationQuestion.setOnClickListener(view -> {
            Intent intent = new Intent (Registration.this, Login.class);
            startActivity(intent);
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deklarujemy hasło i email pozbywając się pustych znaków
                String email = registrationEmail.getText().toString().trim();
                String password = registrationPassword.getText().toString().trim();

                //na wypadek, gdyby użytkownik zostawił pole z mailem puste, informujemy go, że jest wymagane
                if (TextUtils.isEmpty(email)){
                    registrationEmail.setError("E-mail jest wymagany!");
                    return;
                }
                //na wypadek, gdyby użytkownik zostawił pole z hasłem puste, informujemy go, że jest wymagane
                if (TextUtils.isEmpty(password)){
                    registrationPassword.setError("Hasło jest wymagane!");
                }else {
                    //po upewnieniu się, ze login i hasło nie są puste, możemy przystąpić do autentykacji Firebase
                    load.setMessage("Rejestracja w toku.");
                    load.setCanceledOnTouchOutside(false);
                    load.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // gdy rejestracja się udała, przenosimy użytkownika do głównego activity z aplikacją
                            Intent intent = new Intent(Registration.this, Home.class);
                            startActivity(intent);
                            finish();
                            load.dismiss();
                        } else {
                            //w przypadku błednej rejestracji informujemy o tym użytkownika oraz wyświetlami błąd za pomocą komunikatu Toast
                            Exception err = task.getException();
                            Toast.makeText(Registration.this, "Rejestracja się nie powiodła. Spróbuj ponownie.\nBłąd:\n"
                                    + err, Toast.LENGTH_SHORT).show();
                            load.dismiss();
                        }
                    });
                }
            }
        });


    }
}