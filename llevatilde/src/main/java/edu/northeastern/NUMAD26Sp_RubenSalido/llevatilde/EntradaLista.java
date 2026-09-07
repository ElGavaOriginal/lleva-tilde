package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

/*
This is a data container for single words entered into the list, 'Mi Lista'.
It stores the canonical word form and its accent classification type, 'tipo'.
The list will display the user-saved word and its type; aguda, grave, esdrújula,
sobresdrújula, and diacrítica.
*/

public class EntradaLista {
    private final String palabra;
    private final String tipo;

    public EntradaLista(String palabra, String tipo) {
        this.palabra = palabra != null ? palabra : "";
        this.tipo = tipo != null ? tipo : "";
    }

    public String getPalabra() { return palabra; }
    public String getTipo() { return tipo; }
}
