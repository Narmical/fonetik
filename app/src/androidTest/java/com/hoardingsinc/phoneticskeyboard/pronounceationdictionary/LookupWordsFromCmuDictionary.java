package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;

import com.hoardingsinc.phoneticskeyboard.R;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.ArpabetToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.CmuPronouncingDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(Parameterized.class)
public class LookupWordsFromCmuDictionary {
    ArpabetToIpaConverter arpabetToIpaConverter;
    Resources res = getInstrumentation().getTargetContext().getResources();

    private Class dictClass;

    public LookupWordsFromCmuDictionary(Class dictClass) {
        this.dictClass = dictClass;
    }

    @Parameterized.Parameters(name = "{index} : {0}")
    public static Collection data() {
        Object[][] data = new Object[][]{
                {InMemoryPronunciationDictionary.class}
                , {RoomPronunciationDictionary.class}
        };
        return Arrays.asList(data);
    }

    @Before
    public void setup() throws IOException {
        this.arpabetToIpaConverter = new ArpabetToIpaConverter(new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.arpabet_to_ipa))));
    }

    @Test
    public void exactMatchOnePronunciation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("AARONSON  EH1 R AH0 N S AH0 N\n");
        assertThat(pronunciationDictionary.exactMatch("εɹʌnsʌn"), contains("aaronson"));
    }

    @Test
    public void exactMatchTwoPronunciations() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        PronunciationDictionary pronunciationDictionary = generateDictionary(
                "AARONSON  EH1 R AH0 N S AH0 N\n" +
                        "AARONSON(1)  AA1 R AH0 N S AH0 N\n");
        assertThat(pronunciationDictionary.exactMatch("εɹʌnsʌn"), contains("aaronson"));
        assertThat(pronunciationDictionary.exactMatch("ɑɹʌnsʌn"), contains("aaronson"));
    }

    @Test
    public void exactMatchThreeSpellings() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("THEIR  DH EH1 R\n" +
                "THERE  DH EH1 R\n" +
                "THEY'RE  DH EH1 R\n");
        assertThat(pronunciationDictionary.exactMatch("ðεɹ"), contains("their", "there", "they're"));
    }

    @Test
    public void lookAheadMatch() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("PLUT  P L UW1 T\n" +
                "PLUSH  P L AH1 SH\n" +
                "PLUTA  P L UW1 T AH0\n" +
                "PLUTH  P L UW1 TH\n" +
                "PLUTO  P L UW1 T OW0\n" +
                "PLUTO'S  P L UW1 T OW0 Z\n" +
                "PLUTOCRAT  P L UW1 T AH0 K R AE2 T\n" +
                "PLUTOCRATS  P L UW1 T AH0 K R AE2 T S\n" +
                "PLUTONIAN  P L UW0 T OW1 N IY0 AH0 N\n");
        assertThat(pronunciationDictionary.getSuggestions("pluːt", 10), contains("plut", "pluta", "pluto", "pluto's", "plutocrat", "plutonian", "plutocrats"));
    }

    private PronunciationDictionary generateDictionary(BufferedReader reader) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (PronunciationDictionary) this.dictClass.getConstructor(Context.class, RawDictionary.class).newInstance(
                ApplicationProvider.getApplicationContext(),
                new CmuPronouncingDictionary(
                        reader,
                        arpabetToIpaConverter)
        );
    }

    private PronunciationDictionary generateDictionary(String dictionary) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return generateDictionary(
                new BufferedReader(
                        new StringReader(dictionary)
                ));
    }

}