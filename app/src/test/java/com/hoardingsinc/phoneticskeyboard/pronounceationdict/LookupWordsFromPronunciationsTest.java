package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;


public class LookupWordsFromPronunciationsTest {
    BufferedReader arpaToIpaReader;

    public LookupWordsFromPronunciationsTest() throws FileNotFoundException {
        this.arpaToIpaReader = new BufferedReader(
                new FileReader("src/main/res/raw/arpabet_to_ipa.txt"));
    }


    @Test
    public void exactMatchOnePronunciation() throws IOException {
        PronunciationDict pronunciationDict = new PronunciationDict(
                arpaToIpaReader,
                new BufferedReader(
                        new StringReader("AARONSON  EH1 R AH0 N S AH0 N\n")
                ));
        assertThat(pronunciationDict.exactMatch("ɛɹʌnsʌn"), contains("aaronson"));
    }

    @Test
    public void exactMatchTwoPronunciations() throws IOException {
        PronunciationDict pronunciationDict = new PronunciationDict(
                arpaToIpaReader,
                new BufferedReader(
                        new StringReader("AARONSON  EH1 R AH0 N S AH0 N\n" +
                                "AARONSON(1)  AA1 R AH0 N S AH0 N\n")
                ));
        assertThat(pronunciationDict.exactMatch("ɛɹʌnsʌn"), contains("aaronson"));
        assertThat(pronunciationDict.exactMatch("ɑɹʌnsʌn"), contains("aaronson"));
    }

    @Test
    public void exactMatchThreeSpellings() throws IOException {
        PronunciationDict pronunciationDict = new PronunciationDict(
                arpaToIpaReader,
                new BufferedReader(
                        new StringReader("THEIR  DH EH1 R\n" +
                                "THERE  DH EH1 R\n" +
                                "THEY'RE  DH EH1 R\n")
                ));
        assertThat(pronunciationDict.exactMatch("ðɛɹ"), contains("their", "there", "they're"));
    }

    @Test
    public void lookAheadMatch() throws IOException {
        PronunciationDict pronunciationDict = new PronunciationDict(
                arpaToIpaReader,
                new BufferedReader(
                        new StringReader("PLUSH  P L AH1 SH\n" +
                                "PLUTA  P L UW1 T AH0\n" +
                                "PLUTH  P L UW1 TH\n" +
                                "PLUTO  P L UW1 T OW0\n" +
                                "PLUTO'S  P L UW1 T OW0 Z\n" +
                                "PLUTOCRAT  P L UW1 T AH0 K R AE2 T\n" +
                                "PLUTOCRATS  P L UW1 T AH0 K R AE2 T S\n" +
                                "PLUTONIAN  P L UW0 T OW1 N IY0 AH0 N\n")
                ));
        assertThat(pronunciationDict.lookaheadMatch("plut"), containsInAnyOrder("pluta", "pluto", "pluto's", "plutocrat", "plutocrats", "plutonian"));
    }
}