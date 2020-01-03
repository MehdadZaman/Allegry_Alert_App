package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {


    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.textView);
        Date date = new Date();
        textView.setText(message +"\n"+date.toString());*/
    }

    public void onClickEmergencyCall(View view){
        makeCall();
    }

    public void makeCall(){
        String number = "+8801982991409";
        if(number.trim().length() >0){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{
                String dial = "tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{
            Toast.makeText(this, "Please Add a Emergency Contact", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makeCall();
            }
            else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickLogout(View v){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

}