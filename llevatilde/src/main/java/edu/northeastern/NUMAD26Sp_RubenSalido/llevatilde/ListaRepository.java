package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 ListaRepository is the responsible for saving and loading the user's word list.
 (Developer's personal note: This was duplicated in llevaTilde and miLista)

 How to use it:
 ListaRepository repo = new ListaRepository(this);
 ArrayList<EntradaLista> lista = repo.cargar();
 repo.guardar(lista);

*/

public class ListaRepository {

    // These are private constants now — no other class needs to know
    // what key we use or what the prefs file is called.
    private static final String PREFS_NAME = "MiListaPrefs";
    private static final String KEY_PALABRAS = "palabras_guardadas_json";
    private static final String TAG = "ListaRepository";

    // Context is needed to open SharedPreferences. We store it but never hold it longer
    // than necessary (the repo object is short-lived).
    private final Context context;

    public ListaRepository(Context context) {
        this.context = context;
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------


    // Loads the saved word list from SharedPreferences. Returns an empty list if nothing
    // has been saved yet, or if the stored JSON is malformed.
    public ArrayList<EntradaLista> cargar() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PALABRAS, "[]");
        ArrayList<EntradaLista> lista = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String palabra = obj.getString("palabra");
                String tipo    = obj.getString("tipo");
                lista.add(new EntradaLista(palabra, tipo));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error al cargar la lista guardada", e);
        }

        return lista;
    }


    // Saves the full word list to SharedPreferences as a JSON string. Entries with serialization
    // errors are skipped and logged instead of crashing.
    public void guardar(ArrayList<EntradaLista> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();

        for (EntradaLista entrada : lista) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("palabra", entrada.getPalabra());
                obj.put("tipo", entrada.getTipo());
                jsonArray.put(obj);
            } catch (JSONException e) {
                Log.e(TAG, "Error al serializar entrada: " + entrada.getPalabra(), e);
            }
        }

        prefs.edit().putString(KEY_PALABRAS, jsonArray.toString()).apply();
    }

    // ------------------------------------------------------------------
    // Convenience method used by llevaTilde
    // ------------------------------------------------------------------

    // Adds a word to the top of the saved list, removes any existing duplicate first to not
    // stored a word twice. Developer's not to self: This was previously an inline method in
    // llevaTilde called guardarPalabraEnMiLista().
    public void agregarAlInicio(String palabra, String tipo) {
        ArrayList<EntradaLista> lista = cargar();
        lista.removeIf(entry -> entry.getPalabra().equalsIgnoreCase(palabra));
        lista.add(0, new EntradaLista(palabra, tipo));
        guardar(lista);
    }
}
