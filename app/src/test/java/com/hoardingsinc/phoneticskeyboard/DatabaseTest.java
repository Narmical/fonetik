package com.hoardingsinc.phoneticskeyboard;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.hoardingsinc.phoneticskeyboard.pronounceationdict.Pronunciation;
import com.hoardingsinc.phoneticskeyboard.pronounceationdict.PronunciationDB;
import com.hoardingsinc.phoneticskeyboard.pronounceationdict.PronunciationDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DatabaseTest {
    private PronunciationDao pronunciationDao;
    private PronunciationDB db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, PronunciationDB.class).build();
        pronunciationDao = db.pronunciationDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writePronunciation() throws Exception {
        Pronunciation pronunciation = new Pronunciation();
        pronunciation.setIpa("Hello");
        pronunciation.setSpellings("hello");
        List<Pronunciation> pronunciationList = new ArrayList<>();
        pronunciationDao.insertAll(pronunciationList);
        List<Pronunciation> allPronunciations = pronunciationDao.getAll();
        assertThat(allPronunciations, equalTo(pronunciationList));
    }
}