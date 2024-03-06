package com.handson.handson.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handson.handson.HandsOn
import com.handson.handson.model.Translation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * History ViewModel
 */
class HistoryViewModel : ViewModel() {

    //Test List
    /*private val testList = listOf<String>("Hello", "whats up", "man", "?")

    private val _historyEntries = MutableStateFlow<List<String>>(testList)
    val historyEntries: StateFlow<List<String>> = _historyEntries.asStateFlow()
*/

    // state of the alert
    private val _showAlertState = MutableStateFlow(false)
    val showAlertState: StateFlow<Boolean> = _showAlertState

    // prepare the SystemService for the clipboard
    val clipboard =
        HandsOn.appContext.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

    /**
     * Enable/Disable the alert
     * @param yes show alert
     * @author Matthias Kroiss
     */
    fun showAlert(yes: Boolean) {
        _showAlertState.value = yes
    }

    /**
     * Get all Translation from the history database
     * @return Flow that contains a list of Translations
     * @author Matthias Kroiss
     */
    fun getAllTranslationEntries(): Flow<List<Translation>> {
        return HandsOn.translationDatabase.translationDao().getAllTranslations()
    }

    /**
     * Delete the whole translation history
     * @author Matthias Kroiss
     */
    fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            HandsOn.translationDatabase.translationDao().deleteAll()
        }
        //  _historyEntries.value = emptyList()
    }

    /**
     * Delete the specified translation of the history
     * @param translation Translation objet that should be deleted
     * @author Matthias Kroiss
     */
    fun deleteHistoryItem(translation: Translation) {
        viewModelScope.launch(Dispatchers.IO) {
            HandsOn.translationDatabase.translationDao().delete(translation)
        }
    }

    /**
     * Copy text to clipboard
     * @param translationText text that gets copied
     * @author Matthias Kroiss
     */
    fun copyToClipBoard(translationText: String){
        val clip: ClipData = ClipData.newPlainText("translation", translationText)
        clipboard.setPrimaryClip(clip)
    }


}