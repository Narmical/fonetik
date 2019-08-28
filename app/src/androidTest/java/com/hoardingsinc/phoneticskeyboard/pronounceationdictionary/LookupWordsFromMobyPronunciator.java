package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;

import com.hoardingsinc.phoneticskeyboard.R;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.IpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.MobyPronunciator;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.MobyToIpaConverter;
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

//@RunWith(Parameterized.class)
public class LookupWordsFromMobyPronunciator {
    MobyToIpaConverter mobyToIpaConverter;
    Resources res = getInstrumentation().getTargetContext().getResources();

    private Class dictClass;

    /*public LookupWordsFromMobyPronunciator(Class dictClass) {
        this.dictClass = dictClass;
    }*/

    public LookupWordsFromMobyPronunciator() {
        this.dictClass = InMemoryPronunciationDictionary.class;
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
        this.mobyToIpaConverter = new MobyToIpaConverter(new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.mpront_to_ipa))));
    }

    @Test
    public void exactMatchOnePronunciation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("artwork '/A/rt,w/[@]/rk\n");
        assertThat(pronunciationDictionary.exactMatch("ɑːɹtwɜːɹk"), contains("artwork"));
        assertThat(pronunciationDictionary.exactMatch("ɑːɹtwəɹk"), contains("artwork"));
    }

    @Test
    public void exactMatchGPronunciation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("Gabe g/eI/b\n");
        assertThat(pronunciationDictionary.lookAheadMatch("g"), contains("gabe"));
    }

    @Test
    public void exactMatchTwoPronunciations() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        PronunciationDictionary pronunciationDictionary = generateDictionary(
                "artwork '/A/rt,w/[@]/rk\n" +
                        "Arty '/A/rt/i/\n");
        assertThat(pronunciationDictionary.exactMatch("ɑːɹtwɜːɹk"), contains("artwork"));
        assertThat(pronunciationDictionary.exactMatch("ɑːɹtiː"), contains("arty"));
    }

    @Test
    public void exactMatchThreeSpellings() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("their /D//(@)/r\n" +
                "there /D//(@)/r\n" +
                "they're /D//(@)/r\n");
        assertThat(pronunciationDictionary.exactMatch("ðɛɹ"), contains("their", "there", "they're"));
    }

    @Test
    public void lookAheadMatch() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        PronunciationDictionary pronunciationDictionary = generateDictionary("plut 'pl/u/t\n" +
                "plush pl/@//S/\n" +
                "pluta pl/u/t/@/\n" +
                "pluth pl/u/T/\n" +
                "pluto 'pl/u/t/oU/\n" +
                "pluto's 'pl/u/t/oU/z\n" +
                "plutocrat 'pl/u/t/@/,kr/&/t\n" +
                "plutocrats 'pl/u/t/@/,kr/&/ts\n" +
                "plutonian pl/u/'t/oU/n/i//@/n\n");
        assertThat(pronunciationDictionary.getSuggestions("pluːt", 10), contains("plut", "pluta", "pluto", "pluto's", "plutocrat", "plutonian", "plutocrats"));
    }

    private PronunciationDictionary generateDictionary(BufferedReader reader) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (PronunciationDictionary) this.dictClass.getConstructor(Context.class, RawDictionary.class).newInstance(
                ApplicationProvider.getApplicationContext(),
                new MobyPronunciator(
                        reader,
                        mobyToIpaConverter)
        );
    }

    private PronunciationDictionary generateDictionary(String dictionary) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return generateDictionary(
                new BufferedReader(
                        new StringReader(dictionary)
                ));
    }

}