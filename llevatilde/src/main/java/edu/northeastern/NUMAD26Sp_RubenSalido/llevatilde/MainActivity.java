package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private Button buttonWidget;
    private Button buttonAbout;
    private Button buttonLlevaTilde;
    private Button buttonMiLista;
    private Button buttonLasReglas;
    private Button buttonDefinicion;
    private Button buttonSalir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonWidget = findViewById(R.id.buttonWidget);
        buttonAbout = findViewById(R.id.buttonInfo);
        buttonLlevaTilde = findViewById(R.id.buttonLlevaTilde);
        buttonMiLista = findViewById(R.id.buttonMiLista);
        buttonLasReglas = findViewById(R.id.buttonLasReglas);
        buttonDefinicion = findViewById(R.id.buttonDefinicion);
        buttonSalir = findViewById(R.id.buttonSalir);


        buttonWidget.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, installWidget.class);
                    startActivity(intent);
        });

        buttonAbout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, about.class);
            startActivity(intent);
        });

        buttonLlevaTilde.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, llevaTilde.class);
            startActivity(intent);
        });

        buttonMiLista.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, miLista.class);
            startActivity(intent);
        });

        buttonLasReglas.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, lasReglas.class);
            startActivity(intent);
        });

        buttonDefinicion.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, definicion.class);
            startActivity(intent);
        });

        buttonSalir.setOnClickListener(view -> finishAffinity());
    }
}