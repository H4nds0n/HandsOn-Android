package com.handson.handson.ui.history

import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.ContentCopy
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.DeleteForever
import androidx.compose.material.icons.twotone.FindInPage
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.handson.handson.HandsOn
import com.handson.handson.R
import com.handson.handson.ui.theme.HandsOnTheme

/**
 * History Composable that displays the translation history of the user
 * @param navController
 * @param historyViewModel viewmodel gets initialized by default
 * @author Matthias Kroiss
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    navController: NavController, historyViewModel: HistoryViewModel = viewModel()
) {

    //val historyEntries by historyViewModel.historyEntries.collectAsState()

    //collect all Entries of the Database as state if during initialization empty list.
    val historyEntries by historyViewModel.getAllTranslationEntries()
        .collectAsState(initial = emptyList())

    // collect the AlertState from the viewmodel
    val showAlertState by historyViewModel.showAlertState.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Translation History")
                },
                actions = {
                    if (historyEntries.isNotEmpty()) {
                        IconButton(onClick = {
                            historyViewModel.showAlert(true)
                        }) {
                            Icon(Icons.TwoTone.DeleteForever, contentDescription = "Delete History")
                        }
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

        // show alert
        if (showAlertState) {
            Alert(
                onDismissRequest = { historyViewModel.showAlert(false) },
                onConfirmation = {
                    historyViewModel.deleteHistory()
                    historyViewModel.showAlert(false)
                },
                dialogTitle = "Delete History",
                dialogText = "This will delete all your saved translations for ever.",
                icon = Icons.TwoTone.Warning
            )
        }

        // if entries are not empty show them
        if (historyEntries.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                contentPadding = PaddingValues(5.dp)
            ) {

                items(historyEntries) {

                    HistoryItem(
                        historyEntry = it.translationText,
                        onCopyClick = {
                            historyViewModel.copyToClipBoard(it.translationText)
                            // Only show a toast for Android 12 and lower.
                            // On newer versions the user gets automatically
                            // informed by the system that something has been
                            // copied to clipboard.
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                Toast.makeText(
                                    HandsOn.appContext,
                                    "Copied to clipboard",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onDeleteClick = { historyViewModel.deleteHistoryItem(it) },
                        modifier = Modifier.padding(5.dp)
                    )

                }
            }
            // else show a screen that informs the user that nothing is in the database
        } else {
            NothingToShow(R.string.nothing_to_show_here)
        }
    }


}

/**
 * Screen that shows a Icón and a Text to inform the user that nothing can be shown at the moment
 * @param text: Stringres of the text that should be shown
 * @param modifier: Modifier (optional)
 * @author Matthias Kroiss
 */
@Composable
fun NothingToShow(@StringRes text: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Icon(
            imageVector = Icons.TwoTone.FindInPage,
            contentDescription = stringResource(text),
            modifier = Modifier.size(70.dp)
        )
        Text(text = stringResource(text), fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@Preview
@Composable
fun NothingToShowPreview() {
    HandsOnTheme {
        Surface {
            NothingToShow(R.string.nothing_to_show_here)
        }
    }
}

/**
 * Simple Alert Dialog that can be shown e.g as a warning before a disruptive action like
 * deleting things
 * @param onDismissRequest gets called when the user presses cancel
 * or outside of the Alert to dismiss it
 * @param onConfirmation gets called when the user presses ok
 * @param dialogText text of the main Alert Dialog
 * @param dialogTitle text of the dialog title
 * @author Matthias Kroiss
 */
@Composable
fun Alert(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Warning Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

/**
 * Card that shows a history item.
 * @param historyEntry Entry that is shown in the Card
 * @param onCopyClick function that gets invoked if the user presses the copy button
 * @param onDeleteClick function that gets invoked if the user presses the delete button
 * @param modifier modifier (optional)
 * @author Matthias Kroiss
 */
@Composable
fun HistoryItem(
    historyEntry: String,
    onCopyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = historyEntry, modifier = Modifier.padding(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { onCopyClick() }) {
                    Icon(
                        imageVector = Icons.TwoTone.ContentCopy,
                        contentDescription = "Copy content"
                    )
                }
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        imageVector = Icons.TwoTone.Delete,
                        contentDescription = "Copy content"
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun HistoryItemPrev() {
    HandsOnTheme {
        HistoryItem(
            "Test",
            { Toast.makeText(HandsOn.appContext, "Hi", Toast.LENGTH_SHORT).show() },
            {})
    }
}



