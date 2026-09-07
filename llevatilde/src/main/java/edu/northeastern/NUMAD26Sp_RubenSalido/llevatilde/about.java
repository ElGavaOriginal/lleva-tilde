package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class about extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        Button buttonCerrarAbout = findViewById(R.id.buttonCerrarAbout);
        buttonCerrarAbout.setOnClickListener(v -> finish());
    }
}
