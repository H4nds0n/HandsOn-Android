package com.handson.handson.ui.quiz

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.handson.handson.HandsOn
import com.handson.handson.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

class QuizViewModel : ViewModel() {

    init {
        downloadMLModel()
    }

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

    private val _mlModelIsReadyState = MutableStateFlow(false)
    val mlModelIsReady: StateFlow<Boolean> = _mlModelIsReadyState

    var mlModel: File? = null
    val conditions = CustomModelDownloadConditions.Builder()
        .build()

    /**
     * Load the current ML Model from the storage and download the latest version from Firebase
     */
    private fun downloadMLModel() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseModelDownloader.getInstance()
                .getModel(
                    "asl_model_mobilenetv2", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                    conditions
                )
                .addOnSuccessListener { model: CustomModel? ->
                    // Download complete. Depending on your app, you could enable the ML
                    // feature, or switch from the local model to the remote model, etc.

                    Log.d("mlModel", "model is ready")
                    // The CustomModel object contains the local path of the model file,
                    // which you can use to instantiate a TensorFlow Lite interpreter.
                    mlModel = model?.file
                    _mlModelIsReadyState.value = true
                }
                .addOnCompleteListener {
                    Log.d("mlModel", "complete")
                }
                .addOnFailureListener{
                    Log.d("mlModel", it.message.toString())
                }
                .addOnCanceledListener(){

                }
        }

    }

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