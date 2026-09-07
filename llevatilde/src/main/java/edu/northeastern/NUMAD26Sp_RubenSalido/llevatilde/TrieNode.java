package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This function goes through a Trie structure to find the canonical form of a word.
 */
public class TrieNode {
    private final Map<Character, TrieNode> children = new HashMap<>();
    private final List<PalabraEntry> entries = new ArrayList<>();

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public List<PalabraEntry> getEntries() {
        return entries;
    }
}
