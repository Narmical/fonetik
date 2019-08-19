package com.hoardingsinc.phoneticskeyboard;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class LoadArpabetToIpaMapTest {

    @Test
    public void loadArpabetToIpaMapTest() throws IOException {
        PronunciationDict dict = new PronunciationDict(null, null);


        Map<String, String> expected = new HashMap<String, String>() {
            {
                put("AA", "ɑ");
                put("AE", "æ");
                put("AH", "ʌ");
            }
        };

        Map<String, String> actual = dict.loadArpabetToIpaMap(new BufferedReader(new StringReader("AA\tɑ\n" +
                "AE\tæ\n" +
                "AH\tʌ")));

        assertThat(expected.entrySet(), everyItem(isIn(actual.entrySet())));
    }

}