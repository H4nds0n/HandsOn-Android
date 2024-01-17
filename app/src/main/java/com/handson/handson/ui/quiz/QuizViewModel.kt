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

    private val _mlModelIsReadyState = MutableStateFlow(false)
    val mlModelIsReady: StateFlow<Boolean> = _mlModelIsReadyState

    var mlModel: File? = null
    val conditions = CustomModelDownloadConditions.Builder()
        .build()

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