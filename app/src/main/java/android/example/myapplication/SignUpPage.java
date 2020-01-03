package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.security.spec.ECField;

public class SignUpPage extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText userName;
    EditText password;

    String firstNameStr;
    String lastNameStr;
    String emailStr;
    String userNameStr;
    String passwordStr;

    TextView invalidSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        firstName = findViewById(R.id.firstNameEnterSignUp);
        lastName = findViewById(R.id.lastNameEnterSignUp);
        email = findViewById(R.id.emailEnterSignUp);
        userName = findViewById(R.id.userNameEnterSignUp);
        password = findViewById(R.id.passwordEnterSignUp);

        invalidSignUp = findViewById(R.id.invalidSignUp);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpClick(View v)
    {
        firstNameStr = firstName.getText().toString();
        lastNameStr = lastName.getText().toString();
        emailStr = email.getText().toString();
        userNameStr = userName.getText().toString();
        passwordStr = password.getText().toString();

        createAccount(emailStr, passwordStr);
    }

    public void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVerification();
                        } else {
                            invalidSignUp.setText("Failed to send verification Email");
                            updateUI(null);
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpPage.this, VerifyEmailPage.class);
                            startActivity(intent);
                        } else {
                            mAuth.getCurrentUser().delete();
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException fauce)
                            {
                                invalidSignUp.setText("There already exists an account with this email");
                            }
                            catch (Exception e)
                            {
                                invalidSignUp.setText("Failed to send verification Email");
                            }
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
        {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        else
        {
            invalidSignUp.setVisibility(View.VISIBLE);
        }
    }
}