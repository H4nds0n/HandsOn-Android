package com.handson.handson.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.handson.handson.HandsOn
import com.handson.handson.R
import kotlin.random.Random

class QuizViewModel : ViewModel() {
    var levelTwo by mutableStateOf(false)
        private set

    var translation by mutableStateOf("")
        private set

    var showCorrect by mutableStateOf(false)
        private set

    var questionLetter by mutableStateOf(generateQuestionLetter())
        private set

    var questionWord by mutableStateOf(generateQuestionWord())
        private set

    var answeredWord by mutableStateOf("")

    var answerCount by mutableIntStateOf(0)
    private set

    fun updateTranslation(question: String, answer: String? = null) {
        translation = if (answer == null) question else "Say: $question \nYour Answer: $answer"
    }

    fun skip() {
        resetWord()
        if (levelTwo) newQuestionWord()
        else newQuestionLetter()
    }

    fun switchLevel() {
        levelTwo = !levelTwo
    }

    fun riseCounter() {
        answerCount++
    }

    fun resetWord() {
        answerCount = 0
        answeredWord = ""
    }

    fun showCorrectAnswer(input: Boolean) {
        showCorrect = input
    }

    fun newQuestionLetter() {
        val oldQuestion = questionLetter
        while (oldQuestion == questionLetter)
            questionLetter = generateQuestionLetter()
    }

    fun newQuestionWord() {
        val oldQuestion = questionWord
        while (oldQuestion == questionWord)
            questionWord = generateQuestionWord()
    }

    private fun generateQuestionLetter(): String {

        val alphabet = HandsOn.appContext.getString(R.string.alphabet)
        val randomIndex = Random.nextInt(alphabet.length)
        return alphabet[randomIndex].toString()
    }

    private fun generateQuestionWord(): String {
        val words = HandsOn.appContext.resources.getStringArray(R.array.question_words)
        val randomIndex = Random.nextInt(words.size)
        return words[randomIndex]
    }


}