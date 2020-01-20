package android.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
//import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.auth.User;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.PendingIntent.getActivity;

public class QRIngredientScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    HashSet<String> ingredientSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qringredient_scanner);

        scannerView = findViewById(R.id.zxscan);
        txtResult = findViewById(R.id.txt_result);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        scannerView.setResultHandler(QRIngredientScanner.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QRIngredientScanner.this, "You must accept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        processRawResult(rawResult.getText());
    }

    public void processRawResult(String s) {
        s = s.toLowerCase();
        if(s.startsWith("ingredients"))
        {
            String ingredients = s.substring(s.indexOf('\n') + 1);
            txtResult.setText("Valid QR Code");
            processIngredients(ingredients);
        }
        else
        {
            txtResult.setText("Invalid QR Code");
            showDialogueInvalidQRCode();
        }
    }

    public void processIngredients(String ingredients)
    {
        String[] ingredientList = ingredients.split(",");
        //Weird cases below
        //String[] ingredientList = ingredients.split("[^a-zA-Z \n]");
        for(int i = 0; i < ingredientList.length; i++) {
            ingredientList[i] = ingredientList[i].trim();
        }
        ingredientSet = new HashSet<>(Arrays.asList(ingredientList));
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final DocumentReference docRef = db.collection("users").document(user.getUid());
        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.SERVER;
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> dataMap = document.getData();
                    ArrayList<String> allergies = (ArrayList<String>)document.get("Food Allergies");
                    processIngredients2(allergies);
                }
            }
        });
    }

    public void processIngredients2(ArrayList<String> allergies)
    {
        boolean allergyAlert = false;
        ArrayList<String> listOfAllergies = new ArrayList<>();
        for(int i = 0; i < allergies.size(); i++)
        {
            if(ingredientSet.contains(allergies.get(i)))
            {
                listOfAllergies.add(allergies.get(i));
                allergyAlert = true;
            }
        }
        if(allergyAlert)
        {
            showDialogueAllergyAlert(listOfAllergies);
        }
        else
        {
            showDialogueNoAllergies();
        }
    }

    public void showDialogueInvalidQRCode()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRIngredientScanner.this);
        builder.setMessage("Invalid QR Code")
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRIngredientScanner.this, QRIngredientScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRIngredientScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    public void showDialogueAllergyAlert(ArrayList<String> arrayOfAllergies)
    {
        String allergenListStr = "";
        for(int i = 0; i < arrayOfAllergies.size(); i++)
        {
            if(i == (arrayOfAllergies.size() - 1))
                allergenListStr += arrayOfAllergies.get(i);
            else
                allergenListStr += arrayOfAllergies.get(i) + ", ";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(QRIngredientScanner.this);
        builder.setMessage("ALLERGY ALERT\nDangerous ingredients: " + allergenListStr)
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRIngredientScanner.this, QRIngredientScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRIngredientScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    public void showDialogueNoAllergies()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRIngredientScanner.this);
        builder.setMessage("No Allergies Found")
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRIngredientScanner.this, QRIngredientScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRIngredientScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }
}