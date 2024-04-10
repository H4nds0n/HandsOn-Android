package com.handson.handson.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.handson.handson.ui.history.History
import com.handson.handson.ui.quiz.LevelSelection
import com.handson.handson.ui.quiz.Quiz
import com.handson.handson.ui.translator.Translator

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost (navController = navController, startDestination = Screen.Translator.route){
        composable(route = Screen.Translator.route){
            Translator(navController)
        }
        composable(route = "${Screen.Quiz.route}/{level}"){ navBackStackEntry ->
            val level = navBackStackEntry.arguments?.getString("level")
            Quiz(navController, level = (level?.toInt()?.minus(1))?:0)
        }
        composable(route = Screen.History.route){
            History(navController)
        }
        composable(route = Screen.Level.route){
            LevelSelection(navController)
        }
        composable(route = "${Screen.Level.route}/{unlocked}"){ navBackStackEntry ->
            val levelUnlocked = navBackStackEntry.arguments?.getString("unlocked")
            LevelSelection(navController, levelUnlocked = (levelUnlocked?.toInt())?:0)
        }
    }
}