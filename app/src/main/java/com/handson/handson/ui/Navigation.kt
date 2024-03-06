package com.handson.handson.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.handson.handson.ui.history.History
import com.handson.handson.ui.quiz.Quiz
import com.handson.handson.ui.translator.Translator

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost (navController = navController, startDestination = Screen.Translator.route){
        composable(route = Screen.Translator.route){
            Translator(navController)
        }
        composable(route = Screen.Quiz.route){
            Quiz(navController)
        }
        composable(route = Screen.History.route){
            History(navController)
        }
    }
}