package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;

import com.hoardingsinc.phoneticskeyboard.R;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProcessCmuDictTest {
    Resources res = getInstrumentation().getTargetContext().getResources();

    @Test
    public void processCmuDictTest() throws IOException {
        Set<String> expected = new TreeSet<>();
        BufferedReader rawFile = new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.cmudict)));

        String thisLine;
        while ((thisLine = rawFile.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] tokens = thisLine.split("  ");
                expected.add(tokens[1].replaceAll("\\d", "").replaceAll("\\W", ""));
            }
        }

        InMemoryPronunciationDictionary dict = new InMemoryPronunciationDictionary(ApplicationProvider.getApplicationContext(),
                new ArpabetToIpaConverter(
                        new BufferedReader(
                                new InputStreamReader(
                                        this.res.openRawResource(R.raw.arpabet_to_ipa)))),
                new BufferedReader(
                        new InputStreamReader(
                                this.res.openRawResource(R.raw.cmudict)))
        );

        // assert that dict object has name number of unique pronunciations as the input file
        assertThat(dict.numEntries(), equalTo(expected.size()));
    }
}
