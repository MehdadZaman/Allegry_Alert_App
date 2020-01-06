package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;

    private TextView incorrectCredentials;

    private String emailStr;
    private String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        email = findViewById(R.id.emailEnter);
        password = findViewById(R.id.passwordEnter);
        incorrectCredentials = findViewById(R.id.incorrectCredentials);
        mAuth = FirebaseAuth.getInstance();
        resumeSession();
    }

    public void logInClick(View v)
    {
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        if(!validateForm())
        {
            return;
        }
        loginUser(emailStr, passwordStr);
    }

    public void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(mAuth.getCurrentUser().isEmailVerified()) {
                                updateUI(user);
                                user.getIdToken(true);
                            }
                            else {
                                incorrectCredentials.setText("Email not verified");
                                updateUI(null);
                            }
                        } else {

                            updateUI(null);
                        }
                    }
                });
    }

    public void signUpHereClick(View v)
    {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }

    public void resumeSession() {
        if(mAuth.getCurrentUser() != null) {
            //super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    public boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(emailStr)) {
            updateUI(null);
            valid = false;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            updateUI(null);
            valid = false;
        }
        return valid;
    }

    public void resetPasswordClick(View v)
    {
        Intent intent = new Intent(this, ResetPasswordPage.class);
        startActivity(intent);
    }

    public void verifyAccountClick(View v)
    {
        Intent intent = new Intent(this, VerifyEmailPage.class);
        startActivity(intent);
    }

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
        {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        else
        {
            incorrectCredentials.setVisibility(View.VISIBLE);
        }
    }
}