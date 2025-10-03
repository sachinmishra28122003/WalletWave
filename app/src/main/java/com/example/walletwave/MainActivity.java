package com.example.walletwave;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (auth.getCurrentUser() != null) {
            // User is already logged in
            Intent i = new Intent(MainActivity.this, HomePage.class);
            startActivity(i);
            finish();  // Finish login screen so user can't go back
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView SignUp = findViewById(R.id.SignUp);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUp.class);
                startActivity(i);
            }
        });
        EditText email = findViewById(R.id.emailEditText);
        EditText pass = findViewById(R.id.passwordEditText);
        Button btn = findViewById(R.id.loginButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String E = email.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if(E.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this,"None of field can be Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.signInWithEmailAndPassword(E,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, HomePage.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "loginFailed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}