package com.example.smartvalve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public DatabaseReference mDatabase;

    Button btnLogin;
    EditText edtUser;
    EditText edtPass;
    String emailReg,passwordReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        btnLogin = (Button) findViewById(R.id.ButtonLogin_Regis);
        edtUser = (EditText) findViewById(R.id.EditTextUsername_Regis);
        edtPass = (EditText) findViewById(R.id.EditTextPassword_Regis);
        mDatabase= FirebaseDatabase.getInstance().getReference();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register_func();
            }
        });
    }

    private void Register_func() {
        emailReg = edtUser.getText().toString();
        passwordReg = edtPass.getText().toString();
        mAuth.createUserWithEmailAndPassword(emailReg, passwordReg)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                /*            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }*/
                            Toast.makeText(Register.this, "Register Completely!", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(Register.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                /*            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);   */
                            Toast.makeText(Register.this, "Register Failed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
