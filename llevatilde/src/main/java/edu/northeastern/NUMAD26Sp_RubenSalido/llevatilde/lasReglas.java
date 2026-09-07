package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class lasReglas extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.las_reglas);

        Button buttonVerRAE = findViewById(R.id.buttonVerRAE);
        buttonVerRAE.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.rae.es/buen-uso-español/la-tilde-diacrítica-en-palabras-monosílabas"));
            startActivity(intent);
        });

        Button buttonCerrar = findViewById(R.id.buttonCerrarLasReglas);
        buttonCerrar.setOnClickListener(v -> finish());
    }
}
