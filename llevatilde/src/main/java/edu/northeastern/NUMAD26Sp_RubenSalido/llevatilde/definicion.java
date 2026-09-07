package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;

public class definicion extends AppCompatActivity {

    private EditText editTextPalabraDefinicion;
    private SpeechHelper speechHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.definicion);

        editTextPalabraDefinicion = findViewById(R.id.editTextPalabraDefinicion);
        Button buttonBuscarLinguee = findViewById(R.id.buttonBuscarLinguee);
        Button buttonCerrarDefinicion = findViewById(R.id.buttonCerrar);

        speechHelper = new SpeechHelper(this, word -> {
            editTextPalabraDefinicion.setText(word);
            editTextPalabraDefinicion.setSelection(word.length());
        });

        configurarSpeechHelper();
        buttonBuscarLinguee.setOnClickListener(view -> abrirLinguee());
        buttonCerrarDefinicion.setOnClickListener(view -> finish());
    }

    public void abrirLinguee() {
        String palabra = editTextPalabraDefinicion.getText().toString().trim();

        if (palabra.isEmpty()) {
            Toast.makeText(this, "Ingresa una palabra para buscar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String palabraCodificada;

        try {
            palabraCodificada = URLEncoder.encode(palabra, "UTF-8");
        } catch (Exception e) {
            palabraCodificada = palabra;
        }

        String url = "https://www.linguee.com/spanish-english/translation/" + palabraCodificada + ".html";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    // Speech to Text:
    private void configurarSpeechHelper() {
        ImageButton buttonMic = findViewById(R.id.buttonMic);
        buttonMic.setOnClickListener(view -> speechHelper.startListening());
    }

    // Handles the result of the SpeechHelper request
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        speechHelper.onRequestPermissionsResult(requestCode, grantResults);
    }

    // Destroys the SpeechHelper when the Activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechHelper.destroy();
    }
}
