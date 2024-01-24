package com.handson.handson.model

import android.util.Log
import androidx.compose.ui.text.toLowerCase

/**
 * Singleton that is used to translate the text input in a list of sign images
 */
object ASLReverseTranslator {

    fun translate(input: String): List<ASLImages> {
        val translation: MutableList<ASLImages> = mutableListOf()
        var inputLowerCase = input.lowercase()
        for (c in inputLowerCase) {
            when (c) {
                ' ' -> translation += ASLImages.SPACE
                'a' -> translation += ASLImages.A
                'b' -> translation += ASLImages.B
                'c' -> translation += ASLImages.C
                'd' -> translation += ASLImages.D
                'e' -> translation += ASLImages.E
                'f' -> translation += ASLImages.F
                'g' -> translation += ASLImages.G
                'h' -> translation += ASLImages.H
                'i' -> translation += ASLImages.I
                'j' -> translation += ASLImages.J
                'k' -> translation += ASLImages.K
                'l' -> translation += ASLImages.L
                'm' -> translation += ASLImages.M
                'n' -> translation += ASLImages.N
                'o' -> translation += ASLImages.O
                'p' -> translation += ASLImages.P
                'q' -> translation += ASLImages.Q
                'r' -> translation += ASLImages.r
                's' -> translation += ASLImages.S
                't' -> translation += ASLImages.T
                'u' -> translation += ASLImages.U
                'v' -> translation += ASLImages.V
                'w' -> translation += ASLImages.W
                'x' -> translation += ASLImages.X
                'y' -> translation += ASLImages.Y
                'z' -> translation += ASLImages.Z
            }
        }
        Log.d("translation", translation.toString())
        return translation
    }

}