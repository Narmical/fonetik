package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.content.Context;

import java.io.BufferedReader;
import java.util.List;

interface PronunciationDictionary {

    List<String> exactMatch(String ipa);

    List<String> lookaheadMatch(String ipa, int maxSuggestions);

    int numEntries();
}
