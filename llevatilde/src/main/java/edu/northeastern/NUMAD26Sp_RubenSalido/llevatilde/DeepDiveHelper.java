package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 DeepDiveHelper launches an AI assistant with a prebuilt prompt about Spanish accent marks.
 This file handles:
    - Prompt construction (with or without sample sentence)
    - URL encoding
    - An AlertDialog asking the user to confirm
    - The browser Intent that opens Claude.ai

 How to use it:
      DeepDiveHelper helper = new DeepDiveHelper(this);
      helper.launch(palabra, oracion);  // oracion is Spanish for sentence, may be empty.

*/

public class DeepDiveHelper {

    private final Context context;

    public DeepDiveHelper(Context context) {
        this.context = context;
    }


    // launch() is the one public method. It builds the prompt, shows the confirmation dialog,
    // and opens the browser if the user confirms.
    //   @param palabra  The cleaned word being looked up (no punctuation)
    //   @param oracion  Optional sample sentence; pass "" if not provided
    public void launch(String palabra, String oracion) {
        String prompt = buildPrompt(palabra, oracion);
        String encodedPrompt = encodePrompt(prompt);
        String claudeUrl = "https://claude.ai/new?q=" + encodedPrompt;

        new AlertDialog.Builder(context)
                .setTitle("Abrir con...")
                .setMessage("La palabra \"" + palabra
                        + "\" será consultada con Claude.ai."
                        + "\n\nEs necesario tener una cuenta en Claude.ai.")
                .setPositiveButton("Claude.ai", (dialog, which) -> abrirEnNavegador(claudeUrl))
                .setNeutralButton("Cancelar", null)
                .show();
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private String buildPrompt(String palabra, String oracion) {
        if (oracion == null || oracion.isEmpty()) {
            return "Show me the variations of the word \""
                    + palabra
                    + "\" in Spanish, including the variations that require accent marks."
                    + " Example: Alabare, Alabaré." + " if the word is not a verb,"
                    + " show its definition and translation into English"
                    + " and any variations in the meaning of the word. (ex. bus, camión, wawa)";
        } else {
            return "Show me the variations of the word \""
                    + palabra
                    + "\" in Spanish, including the variations that require accent marks."
                    + " Example: Humillare, Humillaré."
                    + " Here is a sample sentence which uses the word for context: \""
                    + oracion
                    + " If the word is not a verb, show its definition and translation into English"
                    + " and any variations in the meaning of the word. (ex. buho, tecolote, etc.) "
                    + "\"." ;
        }
    }

    private String encodePrompt(String prompt) {
        try {
            return URLEncoder.encode(prompt, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is always supported on Android; this catch is required
            // by the compiler. Should never trigger.
            return prompt;
        }
    }

    private void abrirEnNavegador(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
