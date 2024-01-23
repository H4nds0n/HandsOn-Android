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
    // indicates, if the user is in the word-training-mode
    var levelTwo by mutableStateOf(false)
        private set

    // content of the translation textfield
    var translation by mutableStateOf("")
        private set

    // true, if the "answer correct"-screen is active
    var showCorrect by mutableStateOf(false)
        private set

    // current question (as letter)
    var questionLetter by mutableStateOf(generateQuestionLetter())
        private set

    // current question (as word)
    var questionWord by mutableStateOf(generateQuestionWord())
        private set

    // current input of the user
    var answeredWord by mutableStateOf("")

    // size of the input from the user
    var answerCount by mutableIntStateOf(0)
    private set

    // sets the given string as text displayed in the text field
    fun updateTranslation(question: String, answer: String? = null) {
        translation = if (answer == null) question else "Say: $question \nYour Answer: $answer"
    }

    // skips the current question and generates a new one
    fun skip() {
        resetWord()
        if (levelTwo) newQuestionWord()
        else newQuestionLetter()
    }

    // switch the mode from word to letter or vice versa
    fun switchLevel() {
        levelTwo = !levelTwo
    }

    // add 1 to the answer count
    fun riseCounter() {
        answerCount++
    }

    // resets the counter and de stored answer
    fun resetWord() {
        answerCount = 0
        answeredWord = ""
    }

    // shows or hides the "answer correct"-screen
    fun showCorrectAnswer(input: Boolean) {
        showCorrect = input
    }

    // generates a new question (as word)
    fun newQuestionLetter() {
        val oldQuestion = questionLetter
        while (oldQuestion == questionLetter)
            questionLetter = generateQuestionLetter()
    }

    // generates a new question (as letter)
    fun newQuestionWord() {
        val oldQuestion = questionWord
        while (oldQuestion == questionWord)
            questionWord = generateQuestionWord()
    }

    // helper-function for generating a new question (as letter)
    private fun generateQuestionLetter(): String {
        val alphabet = HandsOn.appContext.getString(R.string.alphabet)
        val randomIndex = Random.nextInt(alphabet.length)
        return alphabet[randomIndex].toString()
    }

    // helper-function for generating a new question (as word)
    private fun generateQuestionWord(): String {
        val words = HandsOn.appContext.resources.getStringArray(R.array.question_words)
        val randomIndex = Random.nextInt(words.size)
        return words[randomIndex]
    }


}