package com.hoardingsinc.phoneticskeyboard;


import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.hoardingsinc.phoneticskeyboard.pronounceationdictionary.PronunciationDictionary;
import com.hoardingsinc.phoneticskeyboard.pronounceationdictionary.RoomPronunciationDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.ArpabetToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.CmuPronouncingDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.MobyPronunciator;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.MobyToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PhoneticsKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private static PronunciationDictionary mDictionary;
    private boolean caps = false;
    private Thread dictionaryBuilderThread;
    private Keyboard keyboard;
    private KeyboardView kv;
    private CandidateView mCandidateView;
    private boolean mCompletionOn;
    private CompletionInfo[] mCompletions;
    private StringBuilder mComposing;
    private boolean mPredictionOn;
    private List<String> mSuggestions;

    public boolean isWordSeparator(int code) {
        String separators = "\u0020.,;:!?\n()[]*&@{}/<>_+=|&";
        return separators.contains(String.valueOf((char) code));
    }

    public boolean isWordSeparator(CharSequence text) {
        if (text.length() == 1) {
            return isWordSeparator(text.charAt(0));
        }
        return false;
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
                //InputMethodManager inputManager = (InputMethodManager) getSystemService(
                //        INPUT_METHOD_SERVICE);
                //inputManager.showInputMethodPicker();
                this.rotateLayout();
                break;
            case Keyboard.KEYCODE_DELETE:
                //ic.deleteSurroundingText(1, 0);
                handleBackspace();
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                updateCandidates();
                break;
            case -7:
                this.numberSymbolLayout();
                break;
            case -8:
                this.monothongLayout();
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                if (isWordSeparator(primaryCode) || primaryCode == Keyboard.KEYCODE_DONE) {
                    // Handle separator
                    this.handleSeparator();
                    if (primaryCode == Keyboard.KEYCODE_DONE)
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    else
                        ic.commitText(String.valueOf(code), 1);
                } else {
                    //ic.commitText(String.valueOf(code), 1);
                    handleCharacter(primaryCode, keyCodes);
                }
        }
    }

    private void handleSeparator() {
        if (mComposing.length() > 0) {
            if (mSuggestions != null && mSuggestions.size() > 0)
                pickSuggestionManually(0, "");
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

        mPredictionOn = true;
        mCompletionOn = false;
        mCompletions = null;
        mComposing = new StringBuilder();
        if (mDictionary == null && dictionaryBuilderThread == null) {
            Log.d("PhoneticsKeyboard", "Building Pronunciation Dictionary");

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

        setInputView(onCreateInputView());
    }

    @Override
    public void onText(CharSequence text) {
        //getCurrentInputConnection().commitText(text, 1);
        if (this.isWordSeparator(text)) {
            this.handleSeparator();
        }
        mComposing.append(text);
        getCurrentInputConnection().setComposingText(mComposing, 1);
        //commitTyped(getCurrentInputConnection());
        updateCandidates();
    }

    @Override
    public void onWindowHidden() {
        mComposing = new StringBuilder();
    }

    public void pickSuggestionManually(int index) {
        pickSuggestionManually(index, " ");
    }

    public void pickSuggestionManually(int index, String append) {
        if (mCompletionOn && mCompletions != null && index >= 0
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
        try {
            List<RawDictionary> rawDictionaries = new ArrayList<>();
            rawDictionaries.add(
                    new MobyPronunciator(
                            new BufferedReader(
                                    new InputStreamReader(
                                            this.getResources().openRawResource(R.raw.mpron),
                                            "UTF8"
                                    )
                            ),
                            new MobyToIpaConverter(
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    this.getResources().openRawResource(R.raw.mpront_to_ipa),
                                                    "UTF8"
                                            )

                                    )
                            )
                    )
            );
            rawDictionaries.add(
                    new CmuPronouncingDictionary(
                            new BufferedReader(
                                    new InputStreamReader(
                                            this.getResources().openRawResource(R.raw.cmudict),
                                            "UTF8"
                                    )
                            ),
                            new ArpabetToIpaConverter(
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    this.getResources().openRawResource(R.raw.arpabet_to_ipa),
                                                    "UTF8"
                                            )

                                    )
                            )
                    )
            );
            dictionary = new RoomPronunciationDictionary(this, rawDictionaries);
            return dictionary;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
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

    private void handleCharacter(int primaryCode, int[] keyCodes) {
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
                return R.xml.phonetics_normal;
            case KeyboardPreferences.LAYOUT_DIPHTHONGS:
                return R.xml.phonetics_dipthongs;
            case KeyboardPreferences.LAYOUT_NUM_SYMB:
                return R.xml.phonetics_num_symb;
            case KeyboardPreferences.LAYOUT_SHAVIAN:
                return R.xml.phonetics_shavian;
            case KeyboardPreferences.LAYOUT_LEGACY:
                return R.xml.phonetics_legacy;
            default:
                return R.xml.phonetics_normal;
        }
    }

    private void monothongLayout() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        keyboardPreferences.saveLayout(KeyboardPreferences.LAYOUT_NORMAL);
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
            return mDictionary.getSuggestions(strings[0], 10);
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
