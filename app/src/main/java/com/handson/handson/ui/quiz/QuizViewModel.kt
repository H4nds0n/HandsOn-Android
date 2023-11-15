package com.handson.handson.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class QuizViewModel : ViewModel() {
    var translation by mutableStateOf("")
        private set


    var showCorrect by mutableStateOf(false)
        private set

    var question by mutableStateOf(generateQuestion())
        private set

    fun updateTranslation(input: String) {
        translation = input
    }

    fun skip() {
        val oldQuestion = question
        while (oldQuestion == question)
            question = generateQuestion()
    }


    fun showCorrectAnswer(input: Boolean) {
        showCorrect = input
    }

    fun newQuestion(){
        question = generateQuestion()
    }

    private fun generateQuestion(): String {
        val alphabet = "ABC"
        val randomIndex = Random.nextInt(alphabet.length)
        return alphabet[randomIndex].toString()
    }


}