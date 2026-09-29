package com.jabeza.zartklm;

import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ZartKlmService extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private static final String PREFS = "zartklm_prefs";
    private static final String KEY_THEME = "theme_teal";

    private KeyboardView keyboardView;
    private LinearLayout suggestionBar;
    private Keyboard keyboardGasy;
    private Keyboard keyboardSymbols;
    private Keyboard keyboardEmojis;
    private boolean capsLock = false;
    private boolean symbolsMode = false;
    private boolean useTealTheme = false;
    private StringBuilder currentWord = new StringBuilder();

    @Override
    public View onCreateInputView() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        useTealTheme = prefs.getBoolean(KEY_THEME, false);

        int layoutRes = useTealTheme ? R.layout.keyboard_view_teal : R.layout.keyboard_view_dark;
        View root = LayoutInflater.from(this).inflate(layoutRes, null);

        keyboardView = root.findViewById(R.id.keyboardView);
        suggestionBar = root.findViewById(R.id.suggestionBar);

        // Mampiasa keyboard_gasy_alpha.xml foana
        keyboardGasy = new Keyboard(this, R.xml.keyboard_gasy_alpha);
        keyboardSymbols = new Keyboard(this, R.xml.keyboard_symbols);
        keyboardEmojis = new Keyboard(this, R.xml.keyboard_emojis);

        keyboardView.setKeyboard(symbolsMode ? keyboardSymbols : keyboardGasy);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(true);

        return root;
    }

    private void toggleTheme() {
        useTealTheme = !useTealTheme;
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit().putBoolean(KEY_THEME, useTealTheme).apply();
        setInputView(onCreateInputView());
    }

    private void updateSuggestions() {
        suggestionBar.removeAllViews();
        List<String> suggestions = MalagasyDictionary.getSuggestions(currentWord.toString());

        for (String word : suggestions) {
            TextView tv = new TextView(this);
            tv.setText(word);
            tv.setTextColor(getResources().getColor(R.color.candidate_text));
            tv.setPadding(24, 8, 24, 8);
            tv.setTextSize(15);
            tv.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic == null) return;
                ic.deleteSurroundingText(currentWord.length(), 0);
                ic.commitText(word + " ", 1);
                currentWord.setLength(0);
                updateSuggestions();
            });
            suggestionBar.addView(tv);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                if (currentWord.length() > 0) {
                    currentWord.deleteCharAt(currentWord.length() - 1);
                    updateSuggestions();
                }
                break;

            case Keyboard.KEYCODE_SHIFT:
                capsLock = !capsLock;
                keyboardGasy.setShifted(capsLock);
                keyboardView.invalidateAllKeys();
                break;

            case Keyboard.KEYCODE_MODE_CHANGE:
                symbolsMode = !symbolsMode;
                keyboardView.setKeyboard(symbolsMode ? keyboardSymbols : keyboardGasy);
                break;

            case -10: // bouton emoji 😊
                keyboardView.setKeyboard(keyboardEmojis);
                break;

            case -11: // bouton ABC (hiverina any amin'ny litera)
                keyboardView.setKeyboard(keyboardGasy);
                symbolsMode = false;
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new android.view.KeyEvent(
                        android.view.KeyEvent.ACTION_DOWN,
                        android.view.KeyEvent.KEYCODE_ENTER));
                break;

            case 32: // espace / ELANA
                ic.commitText(" ", 1);
                currentWord.setLength(0);
                updateSuggestions();
                break;

            default:
                char code = (char) primaryCode;
                if (capsLock) code = Character.toUpperCase(code);
                ic.commitText(String.valueOf(code), 1);

                if (Character.isLetter(code)) {
                    currentWord.append(Character.toLowerCase(code));
                    updateSuggestions();
                } else {
                    currentWord.setLength(0);
                    updateSuggestions();
                }
        }
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null || text == null) return;
        ic.commitText(text, 1);
        currentWord.setLength(0);
        updateSuggestions();
    }

    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
