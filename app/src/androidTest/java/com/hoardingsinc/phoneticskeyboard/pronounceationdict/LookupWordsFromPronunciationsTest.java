package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.content.Context;
import android.content.res.Resources;

import androidx.room.SkipQueryVerification;
import androidx.test.core.app.ApplicationProvider;

import com.hoardingsinc.phoneticskeyboard.R;

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
import java.util.Set;
import java.util.TreeSet;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;


@RunWith(Parameterized.class)
public class LookupWordsFromPronunciationsTest {
    ArpabetToIpaConverter arpabetToIpaConverter;
    Resources res = getInstrumentation().getTargetContext().getResources();

    private Class dictClass;

    public LookupWordsFromPronunciationsTest(Class dictClass) {
        this.dictClass = dictClass;
    }

    @Parameterized.Parameters(name = "{index} : {0}")
    public static Collection data() {
        Object[][] data = new Object[][]{
                {InMemoryPronunciationDictionary.class},
                {RoomPronunciationDictionary.class}
        };
        return Arrays.asList(data);
    }

    @Before
    public void setup() throws IOException {
        this.arpabetToIpaConverter = new ArpabetToIpaConverter(new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.arpabet_to_ipa))));
    }

    private PronunciationDictionary generateDictionary(BufferedReader reader) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (PronunciationDictionary) this.dictClass.getConstructor(Context.class, ArpabetToIpaConverter.class, BufferedReader.class).newInstance(
                ApplicationProvider.getApplicationContext(),
                arpabetToIpaConverter,
                reader);
    }

    private PronunciationDictionary generateDictionary(String dictionary) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return generateDictionary(
                new BufferedReader(
                        new StringReader(dictionary)
                ));
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
        PronunciationDictionary pronunciationDictionary = generateDictionary("PLUSH  P L AH1 SH\n" +
                "PLUTA  P L UW1 T AH0\n" +
                "PLUTH  P L UW1 TH\n" +
                "PLUTO  P L UW1 T OW0\n" +
                "PLUTO'S  P L UW1 T OW0 Z\n" +
                "PLUTOCRAT  P L UW1 T AH0 K R AE2 T\n" +
                "PLUTOCRATS  P L UW1 T AH0 K R AE2 T S\n" +
                "PLUTONIAN  P L UW0 T OW1 N IY0 AH0 N\n");
        assertThat(pronunciationDictionary.lookaheadMatch("plut", 10), containsInAnyOrder("pluta", "pluto", "pluto's", "plutocrat", "plutocrats", "plutonian"));
    }

}