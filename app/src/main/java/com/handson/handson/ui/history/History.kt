package com.handson.handson.ui.history

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.DeleteForever
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.handson.handson.HandsOn
import com.handson.handson.ui.Screen
import com.handson.handson.ui.theme.HandsOnTheme
import com.handson.handson.ui.translator.TranslatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    navController: NavController, historyViewModel: HistoryViewModel = viewModel()
) {

   //val historyEntries by historyViewModel.historyEntries.collectAsState()
    val historyEntries by historyViewModel.getAllTranslationEntries().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Translation History")
                },
                actions = {
                    IconButton(onClick = { historyViewModel.deleteHistory() }) {
                        Icon(Icons.TwoTone.DeleteForever, contentDescription = "Delete History")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentPadding = PaddingValues(5.dp)
        ) {
            items(historyEntries) {

                HistoryItem(
                    historyEntry = it.translationText,
                    onClick = {
                        Toast.makeText(HandsOn.appContext, "Hi", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(5.dp)
                )

            }


        }
    }


}


@Composable
fun HistoryItem(historyEntry: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() }
    ) {
        Text(text = historyEntry, modifier = Modifier.padding(10.dp))
    }
}

@Preview
@Composable
fun HistoryItemPrev() {
    HistoryItem("Test", { Toast.makeText(HandsOn.appContext, "Hi", Toast.LENGTH_SHORT).show() })
}


@Preview
@Composable
fun HistoryPreview() {
    HandsOnTheme {
        History(navController = rememberNavController())
    }
}


