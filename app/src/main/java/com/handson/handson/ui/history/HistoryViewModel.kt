package com.handson.handson.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handson.handson.HandsOn
import com.handson.handson.model.Translation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    //Test List
    /*private val testList = listOf<String>("Hello", "whats up", "man", "?")

    private val _historyEntries = MutableStateFlow<List<String>>(testList)
    val historyEntries: StateFlow<List<String>> = _historyEntries.asStateFlow()
*/

    fun getAllTranslationEntries(): Flow<List<Translation>> {
        return HandsOn.translationDatabase.translationDao().getAllTranslations()
    }

    fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            HandsOn.translationDatabase.translationDao().delete()
        }
      //  _historyEntries.value = emptyList()
    }


}