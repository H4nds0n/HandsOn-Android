package com.handson.handson.ui.translator

import android.graphics.Bitmap
import android.graphics.Picture
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.handson.handson.HandsOn
import com.handson.handson.R
import com.handson.handson.model.ASLImages
import com.handson.handson.model.ASLReverseTranslator

class TranslatorViewModel : ViewModel() {

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

    private var lastTranslated by mutableStateOf(" ")

    lateinit var testBitmap: Bitmap


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

        if(input != ""){
            if(translationText.isNotEmpty()) {
                if (translationText.last() != input.last() || handout ) {
                    translationText += input
                    handout = false
                } else {
                    shouldShowSnackbar = true
                    snackbarMessage =
                        HandsOn.appContext.getString(R.string.snackbar_TakeHandOutOfPicture)

                }
            }

            else {
                translationText += input
            }
        }else {
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