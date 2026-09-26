package com.jabeza.zartklm;

import java.util.ArrayList;
import java.util.List;

public class MalagasyDictionary {

    // Liste de base de mots malgaches courants — à enrichir progressivement
    private static final String[] WORDS = {
            "aho", "ianao", "izy", "isika", "izahay", "ianareo", "izy ireo",
            "manao", "ahoana", "tsara", "salama", "veloma", "misaotra",
            "azafady", "tompoko", "mba", "ary", "sy", "fa", "rehefa",
            "raha", "noho", "amin'", "ho", "efa", "mbola", "tena",
            "be", "kely", "lehibe", "madinika", "tsy", "eny", "tsia",
            "aiza", "inona", "iza", "rahoviana", "ahoana", "firy",
            "mankasitraka", "mankahery", "faly", "malahelo", "tia",
            "trano", "fianakaviana", "mpianatra", "mpampianatra",
            "finday", "solosaina", "internet", "milaza", "mandeha",
            "mihinana", "misotro", "matory", "miasa", "mianatra"
    };

    public static List<String> getSuggestions(String prefix) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return results;
        String p = prefix.toLowerCase();
        for (String w : WORDS) {
            if (w.startsWith(p)) {
                results.add(w);
                if (results.size() >= 5) break;
            }
        }
        return results;
    }
}
