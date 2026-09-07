package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;
import java.util.List;

public class llevaTilde extends AppCompatActivity {

    private EditText editTextPalabra;
    private EditText editTextOracion;
    private TextView textResultado;

    private PalabraTrie palabraTrie;

    private SpeechHelper speechHelperPalabra;
    private SpeechHelper speechHelperOracion;

    private AccentAnalyzer accentAnalyzer;
    private ListaRepository listaRepository;
    private DeepDiveHelper deepDiveHelper;

    private static final String KEY_RESULTADO = "resultado_texto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lleva_tilde);

        // Initialize objects
        accentAnalyzer  = new AccentAnalyzer();
        listaRepository = new ListaRepository(this);
        deepDiveHelper  = new DeepDiveHelper(this);

        palabraTrie = LexiconLoader.loadFromJSON(this, "diccionario_tilde_json.json");

        editTextPalabra = findViewById(R.id.editTextPalabra);
        editTextOracion = findViewById(R.id.editTextOracion);
        textResultado   = findViewById(R.id.textResultado);

        // Initialize speech helpers
        speechHelperPalabra = new SpeechHelper(this, word -> {
            editTextPalabra.setText(word);
            editTextPalabra.setSelection(word.length());
        });

        speechHelperOracion = new SpeechHelper(this, word -> {
            editTextOracion.setText(word);
            editTextOracion.setSelection(word.length());
        });

        // Restore text from saved state if it exists
        if (savedInstanceState != null) {
            textResultado.setText(savedInstanceState.getString(KEY_RESULTADO, ""));
        }

        configurarSpeechHelper1();
        configurarSpeechHelper2();
        configurarBuscar();
        configurarDefinicion();
        configurarNuevaBusqueda();
        configurarAgregarMiLista();
        configurarDeepDive();
        configurarSalir();
    }

    // Recalls the saved state if it exists
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_RESULTADO, textResultado.getText().toString());
    }

    // Speech to Text for Palabra Mic Button
    private void configurarSpeechHelper1() {
        ImageButton buttonMic1 = findViewById(R.id.buttonMic1);
        buttonMic1.setOnClickListener(view -> speechHelperPalabra.startListening());
    }

    // Speech to Text for Oracion Mic Button
    private void configurarSpeechHelper2() {
        ImageButton buttonMic2 = findViewById(R.id.buttonMic2);
        buttonMic2.setOnClickListener(view -> speechHelperOracion.startListening());
    }

    // Handles the result of the SpeechHelper request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        speechHelperPalabra.onRequestPermissionsResult(requestCode, grantResults);
        speechHelperOracion.onRequestPermissionsResult(requestCode, grantResults);
    }

    // Destroys the SpeechHelper when the Activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        speechHelperPalabra.destroy();
        speechHelperOracion.destroy();
    }

    // Handles search button logic and displays results
    private void configurarBuscar() {
        Button buttonBuscar = findViewById(R.id.buttonBuscar);

        buttonBuscar.setOnClickListener(view -> {
            hideKeyboard();

            String palabraOriginal = editTextPalabra.getText().toString();

            if (palabraOriginal.isEmpty()) {
                textResultado.setText("Ingresa una palabra para buscar.");
                return;
            }

            String palabraLimpia = TextNormalizer.cleanLettersOnly(palabraOriginal);

            if (palabraLimpia.isEmpty()) {
                textResultado.setText("Ingresa una palabra válida para buscar.");
                return;
            }

            String inputKey = TextNormalizer.toInputKey(palabraLimpia);
            List<PalabraEntry> matches = palabraTrie.search(inputKey);

            if (!matches.isEmpty()) {
                mostrarResultadoDesdeDiccionario(palabraLimpia, matches);
            } else {
                mostrarResultadoDesdeReglas(palabraLimpia);
            }
        });
    }

    // Handles definicion button logic and transfers 'palabra codificada' to linguee.com
    private void configurarDefinicion() {
        Button buttonDefinicion = findViewById(R.id.buttonDefinir);

        buttonDefinicion.setOnClickListener(view -> {
            String palabra = editTextPalabra.getText().toString().trim();

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

            String url = "https://www.linguee.com/spanish-english/translation/"
                    + palabraCodificada + ".html";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }

    // Initiates a new search, erases all text and focuses back to the palabra EditText with keyboard
    // display
    private void configurarNuevaBusqueda() {
        Button buttonNuevaBusqueda = findViewById(R.id.buttonNuevaBusqueda);

        buttonNuevaBusqueda.setOnClickListener(view -> {
            editTextPalabra.setText("");
            editTextOracion.setText("");
            editTextPalabra.requestFocus();
            showKeyboard(editTextPalabra);
            textResultado.setText("");
        });
    }

    // Allows user to add word to Mi Lista
    private void configurarAgregarMiLista() {
        Button buttonAgregarMiLista = findViewById(R.id.buttonAgregarMiLista);

        buttonAgregarMiLista.setOnClickListener(view -> {
            String palabraOriginal = editTextPalabra.getText().toString().trim();

            if (palabraOriginal.isEmpty()) {
                Toast.makeText(this, "No hay palabra para guardar.", Toast.LENGTH_SHORT).show();
                return;
            }

            String palabraLimpia = TextNormalizer.cleanLettersOnly(palabraOriginal);
            String inputKey = TextNormalizer.toInputKey(palabraLimpia);
            List<PalabraEntry> matches = palabraTrie.search(inputKey);

            String palabraParaGuardar;
            String tipoParaGuardar;

            if (!matches.isEmpty()) {
                palabraParaGuardar = matches.get(0).getCanonicalForm();
                tipoParaGuardar = matches.get(0).getTipo();
            } else {
                palabraParaGuardar = palabraLimpia;
                AccentAnalyzer.AccentResult result = accentAnalyzer.analyze(palabraLimpia);
                tipoParaGuardar = result.tipo;
            }

            listaRepository.agregarAlInicio(palabraParaGuardar, tipoParaGuardar);

            Toast.makeText(this, "Palabra agregada a Mi Lista", Toast.LENGTH_SHORT).show();
        });
    }

    // Takes user to Deep Dive activity, which carries 'palabra codificada' to claude.ai
    private void configurarDeepDive() {
        Button buttonDeepDive = findViewById(R.id.buttonDeepDive);

        buttonDeepDive.setOnClickListener(view -> {
            String palabraOriginal = editTextPalabra.getText().toString().trim();

            if (palabraOriginal.isEmpty()) {
                Toast.makeText(this, "Ingresa una palabra primero.", Toast.LENGTH_SHORT).show();
                return;
            }

            String palabraLimpia = TextNormalizer.cleanLettersOnly(palabraOriginal);
            String oracion = editTextOracion.getText().toString().trim();

            deepDiveHelper.launch(palabraLimpia, oracion);
        });
    }

    private void configurarSalir() {
        Button buttonSalirLlevaTilde = findViewById(R.id.buttonSalirLlevaTilde);
        buttonSalirLlevaTilde.setOnClickListener(view -> finish());
    }

    // Displays the results of the word being searched
    private void mostrarResultadoDesdeReglas(String palabra) {
        AccentAnalyzer.AccentResult r = accentAnalyzer.analyze(palabra);

        String resultado =
                "Palabra ingresada: " + palabra + "\n\n"
                        + "Sílabas: " + r.getSilabas() + "\n"
                        + "Número de sílabas: " + r.silabas.size() + "\n"
                        + "Sílaba tónica: " + r.silabaTonica + "\n\n"
                        + "Tipo: " + r.tipo + "\n\n"
                        + "¿Requiere tilde?: " + (r.tieneTilde ? "✔ Sí" : "✘ No") + "\n\n"
                        + "Regla:\n" + r.regla
                        + "Categoría: General";

        textResultado.setText(resultado);
    }

    // Displays the results of the word being searched from the dictionary
    private void mostrarResultadoDesdeDiccionario(String palabraIngresada,
                                                  List<PalabraEntry> matches) {
        if (matches.size() == 1) {
            PalabraEntry entry = matches.get(0);
            String canonical = entry.getCanonicalForm();
            boolean tieneTilde = canonical.matches(".*[áéíóúÁÉÍÓÚ].*");

            AccentAnalyzer.AccentResult r = accentAnalyzer.analyze(canonical);

            String resultado =
                    "Palabra ingresada: " + palabraIngresada + "\n"
                            + "Forma correcta: " + canonical + "\n\n"
                            + "Sílabas: " + r.getSilabas() + "\n"
                            + "Número de sílabas: " + r.silabas.size() + "\n"
                            + "Sílaba tónica: " + entry.getSilabaTonica() + "\n\n"
                            + "Tipo: " + entry.getTipo() + "\n\n"
                            + "¿Requiere tilde?: " + (tieneTilde ? "✔ Sí" : "✘ No") + "\n\n"
                            + "Regla:\n" + entry.getRegla();

            if (!entry.getCategoria().isEmpty()) {
                resultado += "Categoría: " + entry.getCategoria();
            }

            textResultado.setText(resultado);

        } else {
            StringBuilder resultado = new StringBuilder();

            resultado.append("Palabra ingresada: ").append(palabraIngresada).append("\n\n");
            resultado.append("Se encontraron ").append(matches.size()).append(" variantes:\n\n");

            for (PalabraEntry entry : matches) {
                boolean tieneTilde = entry.getCanonicalForm().matches(".*[áéíóúÁÉÍÓÚ].*");

                resultado.append("* ").append(entry.getCanonicalForm())
                        .append(" (").append(entry.getCategoria()).append(")\n");
                resultado.append("  Sílaba tónica: ").append(entry.getSilabaTonica()).append("\n");
                resultado.append("  Tipo: ").append(entry.getTipo()).append("\n");
                resultado.append("  ¿Tilde? ").append(tieneTilde ? "✔ Sí" : "✘ No").append("\n\n");
            }

            textResultado.setText(resultado.toString());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showKeyboard(android.view.View view) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
