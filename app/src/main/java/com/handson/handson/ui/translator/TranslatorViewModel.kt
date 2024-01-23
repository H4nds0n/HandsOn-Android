package com.handson.handson.ui.translator

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.handson.handson.HandsOn
import com.handson.handson.R
import com.handson.handson.model.ASLImages
import com.handson.handson.model.ASLReverseTranslator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class TranslatorViewModel : ViewModel() {

    init {
        downloadMLModel()
    }

    var translationText by mutableStateOf("")
        private set

    var showReverseTranslation by mutableStateOf(false)
        private set

    private val reverseTranslator = ASLReverseTranslator

    var reverseTranslationImages = listOf<ASLImages>()

    var shouldShowSnackbar by mutableStateOf(false)
    var snackbarMessage by mutableStateOf("")
        private set

    var handout by mutableStateOf(false)
        private set


    lateinit var testBitmap: Bitmap

    private val _mlModelIsReadyState = MutableStateFlow(false)
    val mlModelIsReady: StateFlow<Boolean> = _mlModelIsReadyState

    var mlModel: File? = null
    val conditions = CustomModelDownloadConditions.Builder()
        .build()

    // TODO: should be in central MLModel repo or sth with StateFlows to be observable from the
    //  viewmodels then
    private fun downloadMLModel() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseModelDownloader.getInstance()
                .getModel(
                    "test_model", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                    conditions
                )
                // does not work after downloading  because of a bug in the firebase library (securityException in Android 14+)
                // will be fixed in a future release of the firebase module
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
                    Log.e("mlModel", it.message.toString())
                }
                .addOnCanceledListener(){
                }
        }

    }





    fun reverseTranslate() {
        reverseTranslationImages = reverseTranslator.translate(translationText)
    }

    /**
     * Only add char to textfield when it is different from the last one in the textfield.
     * To use the same char take hand out of the picture and try again.
     */
    fun updateTranslationFromML(input: String) {

        //Log.d("handsIn", handLeftPicture.toString())
        Log.d("handsI", input)
        Log.d("handsL", translationText)

        if (input != "") {
            if (translationText.isNotEmpty()) {
                if (translationText.last() != input.last() || handout) {
                    translationText += input
                    handout = false
                    shouldShowSnackbar = false
                } else {
                    shouldShowSnackbar = true
                    snackbarMessage =
                        HandsOn.appContext.getString(R.string.snackbar_TakeHandOutOfPicture)

                }
            } else {
                handout = false
                translationText += input
            }
        } else {
            handout = true
        }

    }


    fun updateTranslateText(input: String) {
        translationText = input
    }

    fun showReverseTranslation(input: Boolean) {
        showReverseTranslation = input
    }

    fun clearTranslationText() {
        translationText = ""
    }

    fun setBitmap(bitmap: Bitmap) {
        testBitmap = bitmap
    }

}