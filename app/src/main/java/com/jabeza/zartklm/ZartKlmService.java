package com.jabeza.zartklm;

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

    private KeyboardView keyboardView;
    private LinearLayout suggestionBar;
    private Keyboard keyboardAzerty;
    private Keyboard keyboardSymbols;
    private boolean capsLock = false;
    private boolean symbolsMode = false;
    private StringBuilder currentWord = new StringBuilder();

    @Override
    public View onCreateInputView() {
        View root = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null);

        keyboardView = root.findViewById(R.id.keyboardView);
        suggestionBar = root.findViewById(R.id.suggestionBar);

        keyboardAzerty = new Keyboard(this, R.xml.keyboard_azerty);
        keyboardSymbols = new Keyboard(this, R.xml.keyboard_symbols);

        keyboardView.setKeyboard(keyboardAzerty);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(true);

        return root;
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
                keyboardAzerty.setShifted(capsLock);
                keyboardView.invalidateAllKeys();
                break;

            case Keyboard.KEYCODE_MODE_CHANGE:
                symbolsMode = !symbolsMode;
                keyboardView.setKeyboard(symbolsMode ? keyboardSymbols : keyboardAzerty);
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new android.view.KeyEvent(
                        android.view.KeyEvent.ACTION_DOWN,
                        android.view.KeyEvent.KEYCODE_ENTER));
                break;

            case 32: // espace
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

    // Gestion des accents (popup au clic sur un caractère alternatif)
    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null || text == null) return;
        ic.commitText(text, 1);
        currentWord.setLength(0);
        updateSuggestions();
    }

    // Méthodes obligatoires de l'interface (non utilisées ici)
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
