package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

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
    void insertAll(List<Pronunciation> products);

    @Update
    void update(Pronunciation product);

    @Delete
    void delete(Pronunciation product);

    @Query("SELECT * FROM pronunciation WHERE ipa LIKE :ipa LIMIT 1")
    List<Pronunciation> getLikeIpa(String ipa);
}