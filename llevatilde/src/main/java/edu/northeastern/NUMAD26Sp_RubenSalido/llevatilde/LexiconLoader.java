package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/*
This function loads lexicon from a JSON file in the src/main/assets file.
The JSON must have the structure of:
    {
    "palabras": [
    {
    "palabra":          "música",
    "silabaTonica":     "mú",
    "tipo":             "Esdrújula",
    "regla":            "Las palabras esdrújula siempre llevan tilde",
    "diacritica":       false,
    "categoria":        "general"
    }],
    }
*/
public class LexiconLoader {
    private static final String TAG = "LexiconLoader";

    public static PalabraTrie loadFromJSON(Context context, String assetFileName) {
        PalabraTrie trie = new PalabraTrie();

        // Read the JSON file as a String
        String jsonString = readAssetFile(context, assetFileName);
        if (jsonString == null) {
            Log.e(TAG, "MNo se pudo leer el archivo: " + assetFileName);
            return trie;
        }

        // Parse the JSON file
        try {
            JSONObject root =       new JSONObject(jsonString);
            JSONArray palabras =    root.getJSONArray("palabras");

            for (int i = 0; i < palabras.length(); i++) {
                JSONObject obj = palabras.getJSONObject(i);

                // Required fields
                String canonical = obj.optString("palabra", "").trim();
                if (canonical.isEmpty()) continue;

                // Optional fields
                String silabaTonica =   obj.optString("silabaTonica", "");
                String tipo =           obj.optString("tipo", "");
                String regla =          obj.optString("regla", "");
                boolean diacritica =    obj.optBoolean("diacritica", false);
                String categoria =      obj.optString("categoria", "");

                // Generate the search word without accent marks and lowercase
                String inputKey = TextNormalizer.toInputKey(canonical);
                if (inputKey.isEmpty()) continue;

                PalabraEntry entry = new PalabraEntry(
                        inputKey,
                        canonical,
                        silabaTonica,
                        tipo,
                        regla,
                        diacritica,
                        categoria
                );

                trie.insert(entry);
            }
            Log.d(TAG, "Dictionary loaded: " + palabras.length() + " entries from " + assetFileName);

        } catch (Exception e) {
            Log.e(TAG, "Error parsing the JSON file: " + assetFileName, e);
        }

        return trie;
    }

    // Read the entire contents of the file in assets as String

    private static String readAssetFile(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error reading the file: " + fileName, e);
            return null;
        }
    }
}
