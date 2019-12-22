package com.narmical.fonetic;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.narmical.fonetic.pronounceationdictionary.Pronunciation;
import com.narmical.fonetic.pronounceationdictionary.PronunciationDB;
import com.narmical.fonetic.pronounceationdictionary.PronunciationDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
public class TestDatabase {
    private PronunciationDB db;
    private PronunciationDao pronunciationDao;

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, PronunciationDB.class).build();
        pronunciationDao = db.pronunciationDao();
    }

    @Test
    public void testGetOne() throws Exception {
        Pronunciation pronunciation = new Pronunciation();
        pronunciation.setIpa("Hello");
        pronunciation.setSpelling("hello");
        List<Pronunciation> pronunciationList = new ArrayList<>();
        pronunciationList.add(pronunciation);
        pronunciationDao.insertAll(pronunciationList);
        List<Pronunciation> actual = pronunciationDao.get("Hello");
        assertThat(actual.get(0).getIpa(), equalTo("Hello"));
    }

    @Test
    public void testLikePronunciation() throws Exception {

        List<Pronunciation> pronunciationList = new ArrayList<>();
        List<Pronunciation> expected = new ArrayList<>();

        List<String> codas = new ArrayList<String>(Arrays.asList("r", "s", "t", "l", "n", "e"));

        for (String coda : codas) {
            Pronunciation pronunciation = new Pronunciation();
            pronunciation.setIpa("Hell" + coda);
            pronunciation.setSpelling("hell" + coda);
            pronunciationList.add(pronunciation);
            expected.add(pronunciation);
        }

        for (String coda : codas) {
            Pronunciation pronunciation = new Pronunciation();
            pronunciation.setIpa("Hall" + coda);
            pronunciation.setSpelling("hall" + coda);
            pronunciationList.add(pronunciation);
        }


        pronunciationDao.insertAll(pronunciationList);
        List<Pronunciation> actual = pronunciationDao.getLikeIpa("Hell%");
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testWritePronunciation() throws Exception {
        Pronunciation pronunciation = new Pronunciation();
        pronunciation.setIpa("Hello");
        pronunciation.setSpelling("hello");
        List<Pronunciation> pronunciationList = new ArrayList<>();
        pronunciationList.add(pronunciation);
        pronunciationDao.insertAll(pronunciationList);
        List<Pronunciation> allPronunciations = pronunciationDao.getAll();
        assertThat(allPronunciations, containsInAnyOrder(pronunciationList.toArray()));
    }
}