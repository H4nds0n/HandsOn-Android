package com.handson.handson.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class QuizViewModel: ViewModel() {
    var translation by mutableStateOf("")
        private set

    var skipped by mutableStateOf(false)
        private set

    var showCorrect by mutableStateOf(false)
        private set

    fun updateTranslation(input: String) {
        translation = input
    }

    fun setSkip(skip: Boolean) {
        skipped = skip
    }

    fun showCorrectAnswer(input: Boolean) {
        showCorrect = input
    }


}