package com.hoardingsinc.phoneticskeyboard;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class arpabetToIpaTest {

    PronunciationDict pronunciationDict = new PronunciationDict(new BufferedReader(new StringReader("AA\tɑ\n" +
            "AE\tæ\n" +
            "AH\tʌ")), null);

    public arpabetToIpaTest() throws IOException {
    }

    @Test
    public void arabetToIpaSinge() {
        assertThat(pronunciationDict.arpabetToIpa("AA"), equalTo("ɑ"));
    }

    @Test
    public void arabetToIpaDouble() {
        assertThat(pronunciationDict.arpabetToIpa("AA AA"), equalTo("ɑɑ"));
    }

    @Test
    public void arabetToIpaTwo() {
        assertThat(pronunciationDict.arpabetToIpa("AA AE"), equalTo("ɑæ"));
    }

    @Test
    public void arabetToIpaMissing() {
        assertThat(pronunciationDict.arpabetToIpa("AAAE"), equalTo(null));
    }
}
