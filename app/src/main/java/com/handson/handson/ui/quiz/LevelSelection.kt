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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.handson.handson.ui.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// item displayed in the Level-Selection-List
// Takes a value as id of Level (z.B. 1 for Level 1)
// and a method which is executed when the item is clicked.
@Composable
fun ListItem(value: Int, onItemClick: (Int) -> Unit) {
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

// Level-Selection-List which contains a list-item for every level available.
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelection(navController: NavController, levelUnlocked: Int = 0, viewModel: QuizViewModel = viewModel()) {
    // List for level selection
    val items = (1..4).toList()
    viewModel.levelUnlocked = levelUnlocked

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("HandsOn")
                },
                // Navigate back to the Translator-Screen
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
                ListItem(item) { clickedItem ->
                    // When the clicked level is not unlocked, a message (toast) is shown and access is denied,
                    // if the level is unlocked, the user is routed to the Quiz-Screen.
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