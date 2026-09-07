package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

/*
This function is a data container where it will store the input word along
with its canonical form of the word. This shows where the accent mark should be placed.
*/
public class PalabraEntry {
    private final String inputKey;
    private final String canonicalForm;
    private final String silabaTonica;
    private final String tipo;

    private final String regla;
    private final boolean diacritica;
    private final String categoria;

    public PalabraEntry(
            String inputKey,
            String canonicalForm,
            String silabaTonica,
            String tipo,
            String regla,
            boolean diacritica,
            String categoria) {
        this.inputKey = inputKey;
        this.canonicalForm = canonicalForm;
        this.silabaTonica = silabaTonica != null ? silabaTonica : "";
        this.tipo = tipo != null ? tipo : "";
        this.regla = regla != null ? regla : "";
        this.diacritica = diacritica;
        this.categoria = categoria != null ? categoria : "";
    }
    // Getters

    public String getInputKey() {return inputKey;}
    public String getCanonicalForm() {return canonicalForm;}
    public String getSilabaTonica() {return silabaTonica;}
    public String getTipo() {return tipo;}
    public String getRegla() {return regla;}
    public boolean isDiacritica() {return diacritica;}
    public String getCategoria() {return categoria;}

    @Override
    public String toString() {return canonicalForm;}
}
