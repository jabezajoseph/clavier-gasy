package com.jabeza.zartklm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "zartklm_prefs";
    private static final String KEY_LAYOUT = "layout_preset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 80, 40, 40);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText("ZartKlm — Safidio ny endriky ny clavier");
        title.setTextSize(18);
        title.setPadding(0, 0, 0, 40);
        root.addView(title);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        addOption(root, prefs, "1 — GASY (g a s y b c d h...)", 1);
        addOption(root, prefs, "2 — TANOR/LMPS (l m p s v y z...)", 2);
        addOption(root, prefs, "3 — Alphabétique (a b c d e f g h...)", 3);

        TextView note = new TextView(this);
        note.setText("\nRehefa voafidy, mankanesa ao amin'ny Paramètres > Langues et saisie > Clavier virtuel raha mbola tsy nalefanao ny ZartKlm Keyboard.");
        note.setPadding(0, 40, 0, 0);
        root.addView(note);
    }

    private void addOption(LinearLayout root, SharedPreferences prefs, String label, int value) {
        Button b = new Button(this);
        int current = prefs.getInt(KEY_LAYOUT, 2);
        b.setText(label + (current == value ? "  ✓" : ""));
        b.setOnClickListener(v -> {
            prefs.edit().putInt(KEY_LAYOUT, value).apply();
            b.setText(label + "  ✓ (voatahiry)");
        });
        root.addView(b);
    }
}
