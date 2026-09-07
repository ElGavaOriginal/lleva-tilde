package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import java.util.ArrayList;
import java.util.List;

/*
This function is the engine of the trie structure search.
*/
public class PalabraTrie {
    private final TrieNode root = new TrieNode();

    public void insert(PalabraEntry entry) {
        String key = entry.getInputKey();
        TrieNode current = root;

        for (char c : key.toCharArray()) {
            current = current.getChildren().computeIfAbsent(c, k -> new TrieNode());
        }

        current.getEntries().add(entry);
    }
    public List<PalabraEntry> search(String key) {
        TrieNode current = root;

        for (char c : key.toCharArray()) {
            current = current.getChildren().get(c);
            if (current == null) {
            return new ArrayList<>();
            }
        }
        return current.getEntries();
    }
    public boolean isEmpty() {
        return root.getChildren().isEmpty();
    }
}
