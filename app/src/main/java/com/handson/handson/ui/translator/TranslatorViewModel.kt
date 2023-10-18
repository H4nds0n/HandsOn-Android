package com.handson.handson.ui.translator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TranslatorViewModel: ViewModel() {

    var translation by mutableStateOf("")
        private set

    fun updateTranslation(input: String) {
        translation = input
    }

}