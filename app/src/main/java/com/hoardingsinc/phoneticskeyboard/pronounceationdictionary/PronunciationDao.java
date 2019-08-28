package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PronunciationDao {

    @Query("SELECT * FROM pronunciation")
    List<Pronunciation> getAll();

    @Insert
    void insertAll(List<Pronunciation> pronunciations);

    @Insert
    void insert(Pronunciation pronunciation);

    @Update
    void update(Pronunciation pronunciation);

    @Delete
    void delete(Pronunciation pronunciation);

    @Query("SELECT * FROM pronunciation WHERE ipa = :ipa")
    List<Pronunciation> get(String ipa);

    @Query("SELECT * FROM pronunciation WHERE ipa LIKE :ipa order by length(ipa) LIMIT 10")
    List<Pronunciation> getLikeIpa(String ipa);

    @Query("SELECT count(distinct ipa) from pronunciation")
    int numEntries();
}