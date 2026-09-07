package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import java.util.ArrayList;
import java.util.List;


/*
 accentAnalyzer is a logic class and has no android imports. It's sole purpose is to
 receive a word in Spanish as a plain string, and returns an AccentResult object. The object
 describes the syllable break-down, stress position, accent type, and the rule classification.
*/

public class AccentAnalyzer {

    // ------------------------------------------------------------------
    // Public entry point
    // ------------------------------------------------------------------

    /*
     analyze() is the one public method the Activity calls. It does all the
     work and packages the result into an AccentResult object.

     Encapsulation principle: all the helper methods below are private.
     The caller never needs to know how syllables are split or how the tonic
     index is found — it just receives the finished result.
    */
    public AccentResult analyze(String palabra) {
        List<String> silabas = separarSilabas(palabra);
        boolean tieneTilde = tieneTilde(palabra);
        int indiceTonica = encontrarIndiceTonica(palabra, silabas);

        String silabaTonica = (indiceTonica >= 0 && indiceTonica < silabas.size())
                ? silabas.get(indiceTonica)
                : "No identificada";

        String tipo = tipoPalabra(silabas, indiceTonica);
        String regla = reglaAcentuacion(palabra, silabas, indiceTonica, tipo);

        return new AccentResult(silabas, silabaTonica, tieneTilde, tipo, regla);
    }

    // ------------------------------------------------------------------
    // Result container (inner class)
    // ------------------------------------------------------------------


    // AccentResult is a simple data holder — it stores everything analyze()
    // computes so the Activity can read it without calling multiple methods.
    // Same pattern as PalabraEntry and EntradaLista - only jobe is to carry data between layers.

    public static class AccentResult {
        public final List<String> silabas;
        public final String silabaTonica;
        public final boolean tieneTilde;
        public final String tipo;
        public final String regla;

        public AccentResult(List<String> silabas, String silabaTonica,
                            boolean tieneTilde, String tipo, String regla) {
            this.silabas = silabas;
            this.silabaTonica = silabaTonica;
            this.tieneTilde = tieneTilde;
            this.tipo = tipo;
            this.regla = regla;
        }

        // Convenience method so the Activity can join syllables without
        // knowing the separator character.
        public String getSilabas() {
            return String.join("-", silabas);
        }
    }

    // ------------------------------------------------------------------
    // Private helpers — syllable and accent logic
    // ------------------------------------------------------------------

    private boolean tieneTilde(String palabra) {
        return palabra.matches(".*[áéíóúÁÉÍÓÚ].*");
    }

    private boolean esVocal(char c) {
        return "aeiouáéíóúüAEIOUÁÉÍÓÚÜ".indexOf(c) >= 0;
    }

    private boolean contieneVocalAcentuada(String s) {
        return s.matches(".*[áéíóúÁÉÍÓÚ].*");
    }

    private boolean terminaEnVocalNS(String palabra) {
        if (palabra.isEmpty()) return false;
        char last = Character.toLowerCase(palabra.charAt(palabra.length() - 1));
        return "aeiouáéíóúns".indexOf(last) >= 0;
    }

    private boolean formanMismoNucleo(char v1, char v2) {
        String fuertes = "aeoáéóAEOÁÉÓ";
        boolean v1fuerte = fuertes.indexOf(v1) >= 0;
        boolean v2fuerte = fuertes.indexOf(v2) >= 0;
        // Two strong vowels = hiatus, not a diphthong
        return !(v1fuerte && v2fuerte);
    }

    private List<String> separarSilabas(String palabra) {
        List<String> silabas = new ArrayList<>();
        String p = palabra.toLowerCase();
        int i = 0;

        while (i < p.length()) {
            StringBuilder silaba = new StringBuilder();

            // 1. Onset consonants (ataque)
            while (i < p.length() && !esVocal(p.charAt(i))) {
                silaba.append(p.charAt(i));
                i++;
            }

            // 2. Nucleus vowel(s)
            if (i < p.length() && esVocal(p.charAt(i))) {
                silaba.append(p.charAt(i));
                i++;

                while (i < p.length()
                        && esVocal(p.charAt(i))
                        && formanMismoNucleo(silaba.charAt(silaba.length() - 1), p.charAt(i))) {
                    silaba.append(p.charAt(i));
                    i++;
                }
            }

            // 3. Coda consonants
            int inicioConsonantes = i;
            while (i < p.length() && !esVocal(p.charAt(i))) {
                i++;
            }

            String grupoConsonantes = p.substring(inicioConsonantes, i);

            if (i >= p.length()) {
                silaba.append(grupoConsonantes);
                silabas.add(silaba.toString());
                break;
            }

            if (grupoConsonantes.isEmpty()) {
                silabas.add(silaba.toString());
            } else if (grupoConsonantes.length() == 1) {
                silabas.add(silaba.toString());
                i = inicioConsonantes;
            } else if (grupoConsonantes.length() == 2) {
                String grupo = grupoConsonantes.toLowerCase();
                if (grupo.matches("(pr|pl|br|bl|tr|dr|cr|cl|gr|gl|fr|fl)")) {
                    silabas.add(silaba.toString());
                    i = inicioConsonantes;
                } else {
                    silaba.append(grupoConsonantes.charAt(0));
                    silabas.add(silaba.toString());
                    i = inicioConsonantes + 1;
                }
            } else {
                String ultimasDos = grupoConsonantes
                        .substring(grupoConsonantes.length() - 2)
                        .toLowerCase();
                if (ultimasDos.matches("(pr|pl|br|bl|tr|dr|cr|cl|gr|gl|fr|fl)")) {
                    silaba.append(grupoConsonantes, 0, grupoConsonantes.length() - 2);
                    silabas.add(silaba.toString());
                    i = inicioConsonantes + grupoConsonantes.length() - 2;
                } else {
                    silaba.append(grupoConsonantes, 0, grupoConsonantes.length() - 1);
                    silabas.add(silaba.toString());
                    i = inicioConsonantes + grupoConsonantes.length() - 1;
                }
            }
        }

        return silabas;
    }

    private int encontrarIndiceTonica(String palabra, List<String> silabas) {
        for (int i = 0; i < silabas.size(); i++) {
            if (contieneVocalAcentuada(silabas.get(i))) {
                return i;
            }
        }

        if (silabas.isEmpty()) return -1;

        if (terminaEnVocalNS(palabra)) {
            return Math.max(0, silabas.size() - 2);
        } else {
            return silabas.size() - 1;
        }
    }

    private String tipoPalabra(List<String> silabas, int indiceTonica) {
        if (indiceTonica < 0 || indiceTonica >= silabas.size()) {
            return "No se puede clasificar";
        }
        int posicionDesdeElFinal = silabas.size() - indiceTonica;
        if (posicionDesdeElFinal == 1) return "Aguda";
        if (posicionDesdeElFinal == 2) return "Grave (llana)";
        if (posicionDesdeElFinal == 3) return "Esdrújula";
        return "Sobresdrújula";
    }

    private String reglaAcentuacion(String palabra, List<String> silabas,
                                    int indiceTonica, String tipo) {
        boolean tiene = tieneTilde(palabra);

        if ("Aguda".equals(tipo)) {
            return tiene
                    ? "La palabra es aguda porque la sílaba tónica es la última. "
                    + "Las palabras agudas llevan tilde si terminan en vocal, 'n' o 's'."
                    : "La palabra es aguda porque la sílaba tónica es la última. "
                    + "Las palabras agudas no llevan tilde si terminan en consonante distinta de 'n' o 's'.";
        }
        if ("Grave (llana)".equals(tipo)) {
            return tiene
                    ? "La palabra es grave (llana) porque la sílaba tónica es la penúltima. "
                    + "Las palabras graves llevan tilde cuando NO terminan en vocal, 'n' o 's'."
                    : "La palabra es grave (llana) porque la sílaba tónica es la penúltima. "
                    + "Las palabras graves no llevan tilde cuando terminan en vocal, 'n' o 's'.";
        }
        if ("Esdrújula".equals(tipo)) {
            return "La palabra es esdrújula porque la sílaba tónica es la antepenúltima. "
                    + "Las palabras esdrújulas siempre llevan tilde.";
        }
        if ("Sobresdrújula".equals(tipo)) {
            return "La palabra es sobresdrújula porque la sílaba tónica está antes de la antepenúltima. "
                    + "Las palabras sobresdrújulas siempre llevan tilde.";
        }
        return "No se puede determinar la regla de acentuación.";
    }
}
