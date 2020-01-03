package android.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddAllergensPage extends AppCompatActivity {

    private RelativeLayout scrollViewRelativeLayout;
    private static int textIncrementalID;
    private static int buttonIncrementalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allergens_page);

        scrollViewRelativeLayout = (RelativeLayout) findViewById(R.id.scrollViewRelativeLayout);
        textIncrementalID = 1;
        buttonIncrementalID = 2000;
    }

    public void onClickDoneButton(View view){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void onClickAddButton(View view){
        Spinner spinner = (Spinner) findViewById(R.id.allergenListSpinner);
        String text = spinner.getSelectedItem().toString();

        //Create the Button
        Button button = new Button(this);
        Resources res = this.getResources();
        Drawable image = ResourcesCompat.getDrawable(res, R.drawable.delete,null);
        Drawable image2 = ResourcesCompat.getDrawable(res, R.drawable.black_rectangle_stroke,null);
        button.setBackground(image);
        button.setForeground(image2);
        button.setId(this.buttonIncrementalID);
        button.setWidth(convertDPtoPixels(10));
        button.setHeight(convertDPtoPixels(10));
        RelativeLayout.LayoutParams paramsButton = new RelativeLayout.LayoutParams(convertDPtoPixels(30), convertDPtoPixels(30));
        paramsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if(buttonIncrementalID == 2000){
            paramsButton.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            paramsButton.setMargins(convertDPtoPixels(8),convertDPtoPixels(8),convertDPtoPixels(8),convertDPtoPixels(8));
        }
        else{
            paramsButton.addRule(RelativeLayout.BELOW, this.buttonIncrementalID-1);
            paramsButton.setMargins(convertDPtoPixels(8),0,convertDPtoPixels(8),convertDPtoPixels(8));
        }
        scrollViewRelativeLayout.addView(button, paramsButton);
        this.buttonIncrementalID++;


        //Create the textView
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(20);
        textView.setId(this.textIncrementalID);
        RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsText.addRule(RelativeLayout.RIGHT_OF, this.buttonIncrementalID-1);
        if(textIncrementalID ==1){
            paramsText.setMargins(convertDPtoPixels(8),convertDPtoPixels(8),convertDPtoPixels(8),convertDPtoPixels(8));
            paramsText.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        else{
            paramsText.setMargins(convertDPtoPixels(8),0,convertDPtoPixels(8),convertDPtoPixels(8));
            paramsButton.addRule(RelativeLayout.BELOW, this.textIncrementalID-1);
            paramsText.addRule(RelativeLayout.BELOW, this.buttonIncrementalID-2);
        }
        scrollViewRelativeLayout.addView(textView, paramsText);
        this.textIncrementalID++;
        Toast.makeText(this, text.trim()+" Added!", Toast.LENGTH_SHORT).show();
    }

    public int convertDPtoPixels(int dp){
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,r.getDisplayMetrics());
    }
}