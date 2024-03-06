package com.handson.handson.model

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Entity
data class Translation(
    @ColumnInfo(name = "translationText") val translationText: String,
    @PrimaryKey(autoGenerate = true) val uid: Int = 0
)

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation")
    fun getAllTranslations(): Flow<List<Translation>>
    @Insert
    fun insertAll(vararg translations: Translation)

    @Query("DELETE from translation")
    fun delete()
}

@Database(entities = [Translation::class], version = 1)
abstract class TranslationDatabase: RoomDatabase(){
    abstract fun translationDao(): TranslationDao
}