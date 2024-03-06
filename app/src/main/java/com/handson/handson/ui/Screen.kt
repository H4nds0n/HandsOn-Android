package com.handson.handson.ui

sealed class Screen(val route: String) {

    object Translator: Screen("translator")
    object Quiz: Screen("quiz")
    object History: Screen("history")

}