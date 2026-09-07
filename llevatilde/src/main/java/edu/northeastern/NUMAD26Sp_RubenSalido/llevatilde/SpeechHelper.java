package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

/*
 SpeechHelper wraps Android's SpeechRecognizer API to encapsulate it from llevaTilde so
 it never has to touch ReocngitionListener, RecognizerIntent, or permission checks directly.

 The speech recognizer is set to Mexican Spanish, 'es-MX' to math the app's focus. If 'es-MX'
 is not available it will fall back to the device's default language.

 How to use it:
      SpeechHelper speechHelper = new SpeechHelper();
      speechHelper.startListening();
*/

public class SpeechHelper {

    // This is the request code used when asking for RECORD_AUDIO permission.
    // It must be unique within the Activity — 101 is arbitrary but unlikely
    // to clash with anything else in this project.
    public static final int REQUEST_RECORD_AUDIO = 101;

    // ResultCallback is a simple interface so the Activity can tell us
    // "when you have a word, call this." This is the callback pattern —
    // it avoids SpeechHelper needing a direct reference to any UI widget.
    public interface ResultCallback {
        void onWordRecognized(String word);
    }

    private final Activity activity;
    private final ResultCallback callback;
    private SpeechRecognizer recognizer;

    public SpeechHelper(Activity activity, ResultCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }


    // startListening() is the one public method called from the mic button. It checks for the
    // permission first, if the permission is already granted it listens immediately.
    // If not, it shows the system permission dialog and Android will
    // call onRequestPermissionsResult(), when the user responds, it will call beginRecognition().
    public void startListening() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not yet granted — ask the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO);
        } else {
            // Permission already granted — start immediately
            beginRecognition();
        }
    }

    /*
     The Activity forwards its onRequestPermissionsResult here.
     If the user tapped "Allow", we start listening right away.
     If they tapped "Deny", we show a toast explaining why the feature
     needs the permission.
    */
    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beginRecognition();
            } else {
                Toast.makeText(activity,
                        "Se necesita acceso al micrófono para usar esta función.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // destroy() must be called from the Activity's onDestroy(), otherwise SpeechRecognizer
    // holds a connection to a system service — if it's not released, a resource leak
    // warning in is generated.
    public void destroy() {
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
        }
    }

    // ------------------------------------------------------------------
    // Private — recognition setup and listener
    // ------------------------------------------------------------------

    private void beginRecognition() {
        // Recreate the recognizer each time — reusing one across calls
        // can cause it to get into a bad state on some devices.
        if (recognizer != null) {
            recognizer.destroy();
        }
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        recognizer.setRecognitionListener(new SpanishRecognitionListener());

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use Mexican Spanish. The recognizer falls back gracefully if not available.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Return only the single most likely result — the app uses the top guess.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Short prompt shown in the system's overlay (if any)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di la palabra...");

        Toast.makeText(activity, "Escuchando...", Toast.LENGTH_SHORT).show();
        recognizer.startListening(intent);
    }


    // SpanishRecognitionListener is a private inner class that implements all of the
    // required callbacks, but most of them do nothing because the app only uses
    // onResults (success) and onError (failure). By making it inner, it can access
    // SpeechHelper's fields directly.
    private class SpanishRecognitionListener implements RecognitionListener {

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (matches != null && !matches.isEmpty()) {
                // The API returns a ranked list — index 0 is the best guess.
                // We clean it through TextNormalizer to strip any punctuation
                // the recognizer might have added (e.g. "café," → "café").
                String raw   = matches.get(0);
                String clean = TextNormalizer.cleanLettersOnly(raw);

                if (!clean.isEmpty()) {
                    callback.onWordRecognized(clean);
                } else {
                    Toast.makeText(activity,
                            "No se reconoció ninguna palabra. Intenta de nuevo.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onError(int error) {
            // Map the integer error code to a human-readable Spanish message
            String mensaje;
            switch (error) {
                case SpeechRecognizer.ERROR_NO_MATCH:
                    mensaje = "No se reconoció ninguna palabra. Intenta de nuevo.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    mensaje = "No se detectó voz. Intenta de nuevo.";
                    break;
                case SpeechRecognizer.ERROR_AUDIO:
                    mensaje = "Error de audio. Verifica el micrófono.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    mensaje = "Error de red. El reconocimiento de voz requiere conexión.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    mensaje = "El reconocedor está ocupado. Intenta de nuevo.";
                    break;
                default:
                    mensaje = "Error al reconocer la voz. Intenta de nuevo.";
                    break;
            }
            Toast.makeText(activity, mensaje, Toast.LENGTH_SHORT).show();
        }

        // The remaining callbacks are required by the interface but unused.
        @Override public void onReadyForSpeech(Bundle params) {}
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    }
}
