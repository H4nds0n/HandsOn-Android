package com.handson.handson.ui.translator

import android.graphics.Bitmap
import android.view.translation.Translator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.handson.handson.model.ASLImages
import com.handson.handson.model.ASLReverseTranslator

class TranslatorViewModel : ViewModel() {

    var translationText by mutableStateOf("")
        private set

    var showReverseTranslation by mutableStateOf(false)
        private set

    private val reverseTranslator  = ASLReverseTranslator

    var reverseTranslationImages = listOf<ASLImages>()

    lateinit var testBitmap: Bitmap


    fun reverseTranslate(){
        reverseTranslationImages = reverseTranslator.translate(translationText)
    }

    fun updateTranslation(input: String) {
        translationText += input

    }

    fun updateTranslateText(input: String) {
        translationText = input
    }

    fun showReverseTranslation(input: Boolean) {
        showReverseTranslation = input
    }

    fun clearTranslationText(){
        translationText = ""
    }

    fun setBitmap(bitmap: Bitmap) {
        testBitmap = bitmap
    }

}