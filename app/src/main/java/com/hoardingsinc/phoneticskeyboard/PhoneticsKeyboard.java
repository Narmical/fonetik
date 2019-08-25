package com.hoardingsinc.phoneticskeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.hoardingsinc.phoneticskeyboard.pronounceationdict.PronunciationDict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PhoneticsKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean caps = false;
    private CandidateView mCandidateView;
    private List<String> mSuggestions;
    private boolean mCompletionOn;
    private static PronunciationDict mDictionary;
    private CompletionInfo[] mCompletions;
    private boolean mPredictionOn;
    private StringBuilder mComposing = new StringBuilder();

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }

    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
        } else if (mComposing.length() > 0) {

            if (mPredictionOn && mSuggestions != null && index >= 0) {
                mComposing.replace(0, mComposing.length(), mSuggestions.get(index));
            }
            commitTyped(getCurrentInputConnection());

        }
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

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                List<String> list = this.mDictionary.lookaheadMatch(this.mComposing.toString());
                Log.d("PhoneticsKeyboard", "updateCandidates: " + mComposing.toString());
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
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
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
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
        if (mDictionary == null) {
            Log.d("PhoneticsKeyboard","Building Pronunciation Dictionary");
            try {
                mDictionary = new PronunciationDict(
                        new BufferedReader(
                                new InputStreamReader(
                                        this.getResources().openRawResource(R.raw.arpabet_to_ipa),
                                        "UTF8"
                                )

                        ),
                        new BufferedReader(
                                new StringReader("PLUSH  P L AH1 SH\n" +
                                        "PLUTA  P L UW1 T AH0\n" +
                                        "PLUTH  P L UW1 TH\n" +
                                        "PLUTO  P L UW1 T OW0\n" +
                                        "PLUTO'S  P L UW1 T OW0 Z\n" +
                                        "PLUTOCRAT  P L UW1 T AH0 K R AE2 T\n" +
                                        "PLUTOCRATS  P L UW1 T AH0 K R AE2 T S\n" +
                                        "PLUTONIAN  P L UW0 T OW1 N IY0 AH0 N\n")
                        )
                );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private int keyboardLayoutVersion() {
        KeyboardPreferences keyboardPreferences = new KeyboardPreferences(this);
        switch (keyboardPreferences.getLayout()) {
            case KeyboardPreferences.LAYOUT_NORMAL:
                return R.xml.phonetics_normal;
            case KeyboardPreferences.LAYOUT_EXTENDED:
                return R.xml.phonetics_extended;
            case KeyboardPreferences.LAYOUT_EXTENDED_2:
                return R.xml.phonetics_extended_2;
            case KeyboardPreferences.LAYOUT_COMPACT:
                return R.xml.phonetics_compact;
            default:
                return R.xml.phonetics_normal;
        }
    }

    public boolean isWordSeparator(int code) {
        String separators = "\u0020.,;:!?\n()[]*&@{}/<>_+=|&";
        return separators.contains(String.valueOf((char) code));
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();

        switch (primaryCode) {
            case Keyboard.KEYCODE_MODE_CHANGE:
                InputMethodManager inputManager = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                inputManager.showInputMethodPicker();
                break;
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                if (isWordSeparator(primaryCode)) {
                    // Handle separator
                    if (mComposing.length() > 0) {
                        commitTyped(getCurrentInputConnection());
                    }
                    ic.commitText(String.valueOf(code), 1);
                } else {
                    //ic.commitText(String.valueOf(code), 1);
                    handleCharacter(primaryCode, keyCodes);
                }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        mSuggestions = suggestions;
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
        //getCurrentInputConnection().commitText(text, 1);
        mComposing = new StringBuilder(text);
        commitTyped(getCurrentInputConnection());
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

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        setInputView(onCreateInputView());
    }
}
