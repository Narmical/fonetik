package com.narmical.fonetic;


import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.narmical.fonetic.pronounceationdictionary.PronunciationDictionary;
import com.narmical.fonetic.pronounceationdictionary.RoomPronunciationDictionary;

import java.util.ArrayList;
import java.util.List;

public class PhoneticsKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private static PronunciationDictionary mDictionary;
    private boolean caps = false;
    private Thread dictionaryBuilderThread;
    private boolean justPickedSuggestion;
    private Keyboard keyboard;
    private KeyboardView kv;
    private CandidateView mCandidateView;
    private boolean mCompletionOn;
    private CompletionInfo[] mCompletions;
    private StringBuilder mComposing;
    private boolean mPredictionOn;
    private List<String> mSuggestions;
    private EditorInfo sEditorInfo;

    public boolean isWordSeparator(int code) {
        String separators = "\u0020.,;:!?\n()[]*&@{}/<>_+=|&'\"";
        return separators.contains(String.valueOf((char) code));
    }

    public boolean isEndPunctuation(int code) {
        String separators = ".,;:!?)]}'\"";
        return separators.contains(String.valueOf((char) code));
    }


    public boolean isEmojiCharacter(int code) {
        String separators = ".,;:()[]*&@/<>_+=|&'\"tTDd8Bb";
        return separators.contains(String.valueOf((char) code));
    }

    public boolean isWordSeparator(CharSequence text) {
        if (text.length() == 1) {
            return isWordSeparator(text.charAt(0));
        }
        return false;
    }

    public boolean doesNotStartWithWordSeparator(CharSequence text) {
        if (text.length() == 0) {
            return false;
        } else {
            return !isWordSeparator(text.charAt(0));
        }
    }

    public boolean startsWithEmojiSeperator(CharSequence text) {
        if (text.length() == 0) {
            return false;
        } else {
            return isEmojiCharacter(text.charAt(0));
        }
    }

    public boolean doesNotStartWithEmojiSeparator(CharSequence text) {
        if (text.length() == 0) {
            return false;
        } else {
            return !isEmojiCharacter(text.charAt(0));
        }
    }

    public boolean startsWithWordSeparator(CharSequence text) {
        if (text.length() == 0) {
            return false;
        } else {
            return !doesNotStartWithWordSeparator(text);
        }
    }

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }

    @Override
    public void onComputeInsets(InputMethodService.Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, keyboardLayoutVersion());
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    private void shift(boolean go) {
        caps = go;
    }

    private void toggleShift() {
        caps = !caps;
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                String word = ci.getText().toString();
                if (this.caps) {
                    word = word.substring(0, 1).toUpperCase() + word.substring(1).toUpperCase();
                } else {
                    word = word.toLowerCase();
                }
                if (ci != null) stringList.add(word);
            }
            setSuggestions(stringList, true, true);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();

        switch (primaryCode) {
            case Keyboard.KEYCODE_MODE_CHANGE:
                this.diphthonLayout();
                break;
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();
                break;
            case Keyboard.KEYCODE_SHIFT:
                this.toggleShift();
                updateCandidates();
                break;
            case -7:
                this.numberSymbolLayout();
                break;
            case -8:
                this.monothongLayout();
                break;
            case -9:
                this.qwertyLayout();
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                if ((isWordSeparator(primaryCode) && doesNotStartWithEmojiSeparator(mComposing)) ||
                        primaryCode == Keyboard.KEYCODE_DONE ||
                        primaryCode == 32) {
                    // Handle separator
                    this.autoSelectFirstSpelling("");

                    if (caps) {
                        this.shift(false);
                    }
                    if (primaryCode == Keyboard.KEYCODE_DONE) {
                        switch (sEditorInfo.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                            case EditorInfo.IME_ACTION_GO:
                                ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                                break;
                            case EditorInfo.IME_ACTION_NEXT:
                                ic.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                                break;
                            case EditorInfo.IME_ACTION_SEARCH:
                                ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                                break;
                            case EditorInfo.IME_ACTION_SEND:
                                ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
                                break;
                            default:
                                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                                break;
                        }
                    } else {
                        ic.commitText(String.valueOf(code), 1);
                    }
                } else if (!isWordSeparator(primaryCode) && startsWithWordSeparator(mComposing)) {
                    ic.commitText(mComposing, 1);
                    mComposing.setLength(0);
                    handleCharacter(primaryCode);
                    this.justPickedSuggestion = false;
                } else if (ic.getTextBeforeCursor(1, 0).equals(" ") && mComposing.length() == 0
                        && isEndPunctuation(primaryCode) && this.justPickedSuggestion) {
                    ic.commitText("", -1);
                    ic.commitText(code + " ", 1);
                    this.justPickedSuggestion = false;
                } else {
                    handleCharacter(primaryCode);
                    this.justPickedSuggestion = false;
                }
        }
    }

    private void autoSelectFirstSpelling(String append) {
        if (mComposing.length() > 0) {
            if (mSuggestions != null && mSuggestions.size() > 1)
                pickSuggestionManually(1, append);
            else
                commitTyped(getCurrentInputConnection());
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        this.sEditorInfo = attribute;

        mPredictionOn = true;
        mCompletionOn = false;
        mCompletions = null;
        mComposing = new StringBuilder();
        if (mDictionary == null && dictionaryBuilderThread == null) {

            dictionaryBuilderThread = new Thread(() -> {
                final PronunciationDictionary result = buildDictionary();

                runOnUiThread(() -> mDictionary = result);
            });
            dictionaryBuilderThread.start();
        }
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        this.sEditorInfo = info;
        setInputView(onCreateInputView());

    }

    @Override
    public void onText(CharSequence text) {
        for (int i = 0; i < text.length(); i++) {
            this.onKey((int) text.charAt(i), null);
        }
    }

    @Override
    public void onWindowHidden() {
        mComposing = new StringBuilder();
    }

    public void pickSuggestionManually(int index) {
        pickSuggestionManually(index, " ");
    }

    public void pickSuggestionManually(int index, String append) {
        this.justPickedSuggestion = true;
        if (index == 0) {
            commitTyped(getCurrentInputConnection());
        } else if (mCompletionOn && mCompletions != null && index >= 1
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
        } else if (mComposing.length() > 0) {

            if (mPredictionOn && mSuggestions != null && index >= 0) {
                new RecordSuggestionSelected().execute(mComposing.toString(), mSuggestions.get(index));
                mComposing.replace(0, mComposing.length(), mSuggestions.get(index));
            }
            mComposing.append(append);
            commitTyped(getCurrentInputConnection());

        }

        this.shift(false);
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }

        if (suggestions != null) {
            for (int i = 0; i < suggestions.size(); i++) {
                if (caps) {
                    suggestions.set(i,
                            suggestions.get(i).substring(0, 1).toUpperCase()
                                    + suggestions.get(i).substring(1));
                } else {
                    suggestions.set(i, suggestions.get(i).toLowerCase());
                }
            }
        }

        mSuggestions = suggestions;
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    private PronunciationDictionary buildDictionary() {
        PronunciationDictionary dictionary;
        dictionary = new RoomPronunciationDictionary(this);
        return dictionary;
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, 1);
            mComposing.setLength(0);
            updateCandidates();
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
    }

    private void handleCharacter(int primaryCode) {
        if (mPredictionOn) {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else {
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private int keyboardLayoutVersion() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        switch (keyboardPreferences.getLayout()) {
            case KeyboardPreferences.LAYOUT_NORMAL:
                return R.xml.phonetics_mixed;
            case KeyboardPreferences.LAYOUT_DIPHTHONGS:
                return R.xml.phonetics_dipthongs;
            case KeyboardPreferences.LAYOUT_NUM_SYMB:
                return R.xml.phonetics_num_symb;
            case KeyboardPreferences.LAYOUT_SHAVIAN:
                return R.xml.phonetics_shavian;
            case KeyboardPreferences.LAYOUT_LEGACY:
                return R.xml.phonetics_legacy;
            case KeyboardPreferences.LAYOUT_QWERTY:
                return R.xml.phonetics_qwerty;
            default:
                return R.xml.phonetics_mixed;
        }
    }

    private void monothongLayout() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_NORMAL);
        this.switchLayout();
    }


    private void qwertyLayout() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_QWERTY);
        this.switchLayout();
    }

    private void diphthonLayout() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_DIPHTHONGS);
        this.switchLayout();
    }

    private void numberSymbolLayout() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_NUM_SYMB);
        this.switchLayout();
    }

    private void rotateLayout() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        keyboardPreferences.rotateLayout();
        this.switchLayout();
    }

    private void runOnUiThread(Runnable runnable) {
        runnable.run();
    }

    private void switchLayout() {
        this.keyboard = new Keyboard(this, keyboardLayoutVersion());
        this.kv.setKeyboard(this.keyboard);
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */

    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0 && mDictionary != null) {
                new GetSuggestions().execute(mComposing.toString());

            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    private class GetSuggestions extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            List<String> list = mDictionary.getSuggestions(strings[0], 10);
            list.add(0, "✔");
            if (isWordSeparator(strings[0]) && strings[0].length() == 1) {
                list.add(1, strings[0]);
            }
            return list;
        }

        protected void onPostExecute(List<String> result) {
            setSuggestions(result, true, true);
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class RecordSuggestionSelected extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            mDictionary.recordSpellingSelected(strings[0] + "%", strings[1]);
            return true;
        }

        protected void onPostExecute(List<String> result) {
            setSuggestions(result, true, true);
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }
}
