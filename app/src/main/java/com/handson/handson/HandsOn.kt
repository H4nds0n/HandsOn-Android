package com.handson.handson

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.handson.handson.model.Translation
import com.handson.handson.model.TranslationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HandsOn : Application() {
    companion object {
        lateinit var appContext: Context
            private set
        lateinit var translationDatabase: TranslationDatabase

    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        runBlocking {
            withContext(Dispatchers.IO) {
                translationDatabase = Room.databaseBuilder(
                    HandsOn.appContext,
                    TranslationDatabase::class.java, "translations-db"
                ).build()

                //Only for testing
               /* var translationDao = translationDatabase.translationDao()
                translationDao.insertAll(
                    Translation("This"),
                    Translation("is an"),
                    Translation("Test")
                )*/

            }
        }
    }
}