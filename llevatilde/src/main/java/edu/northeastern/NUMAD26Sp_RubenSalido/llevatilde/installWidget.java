package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class installWidget extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_widget);

        Button buttonCerrarWidgetInstall = findViewById(R.id.buttonCerrarWidgetInstall);
        buttonCerrarWidgetInstall.setOnClickListener(v -> finish());
    }
}
