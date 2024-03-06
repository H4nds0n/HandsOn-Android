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


/**
 * Translation data class that holds the translation-text and a uid.
 * It is a Room Data Entity
 * @author Matthias Kroiss
 */
@Entity
data class Translation(
    @ColumnInfo(name = "translationText") val translationText: String,
    @PrimaryKey(autoGenerate = true) val uid: Int = 0
)

/**
 * Room DAO for the Translation history
 * Provides the most currently important operations like select all, insert all,
 * delete all and delete one
 * @author Matthias Kroiss
 */
@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation")
    fun getAllTranslations(): Flow<List<Translation>>
    @Insert
    fun insertAll(vararg translations: Translation)

    @Query("DELETE from translation")
    fun deleteAll()

    @Delete
    fun delete(translation: Translation)
}

/**
 * Room Database class that manages the database for the history
 * @author Matthias Kroiss
 */
@Database(entities = [Translation::class], version = 1)
abstract class TranslationDatabase: RoomDatabase(){
    abstract fun translationDao(): TranslationDao
}