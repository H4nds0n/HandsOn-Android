package com.handson.handson.ui.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.handson.handson.ui.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ListItemWithDifferentiation(value: Int, onItemClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(value) }
    ) {
        Text(
            text = "Level $value",
            modifier = Modifier.padding(16.dp),
            color = Color.LightGray
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelection(navController: NavController, levelUnlocked: Int = 0, viewModel: QuizViewModel = viewModel()) {
    val items = (1..4).toList()
    viewModel.levelUnlocked = levelUnlocked
    Log.d("Quiz", "Level-unlocked: $levelUnlocked")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("HandsOn")
                },
                actions = {

                    IconButton(onClick = { viewModel.switchLevel() }) {
                        Icon(
                            imageVector = Icons.Filled.TextIncrease,
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Translator.route) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized Description"
                        )
                    }
                }
            )
        },
    ){
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 75.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                ListItemWithDifferentiation(item) { clickedItem ->
                    if(levelUnlocked < clickedItem-1)
                        showToast(context = navController.context, message = "Level is not unlocked")
                    else {
                        viewModel.selectedLevel = clickedItem
                        navController.navigate("${Screen.Quiz.route}/$clickedItem")
                    }
                }
            }
        }
    }
}

private fun showToast(context: Context, message: String) {
    CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}