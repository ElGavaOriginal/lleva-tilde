package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import java.text.Normalizer;
import java.util.Locale;

/*
This function removes all punctuation/grammatical conventions except for letters.
ex. "café" -> clean -> "cafe"
*/
public class TextNormalizer {
    public static String cleanLettersOnly(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("[^\\p{L}]", "");
    }

    public static String removeAccents(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    public static String toInputKey(String text) {
        String cleaned = cleanLettersOnly(text).toLowerCase(Locale.ROOT);
        return removeAccents(cleaned);
    }
}
