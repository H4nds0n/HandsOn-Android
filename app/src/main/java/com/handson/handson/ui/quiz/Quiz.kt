package com.handson.handson.ui.quiz

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.handson.handson.ui.Screen
import com.handson.handson.ui.translator.Camera
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Quiz(navController: NavController, quizViewModel: QuizViewModel = viewModel(), level: Int = 0) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    val modelReady by quizViewModel.mlModelIsReady.collectAsState()

    // Not perfect but suitable for the moment
    // refactored to use the current orientation as initial value to solve the problem with layout
    // shift on orientation change.
    var orientation by remember { mutableIntStateOf(activity!!.resources.configuration.orientation) }

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    LaunchedEffect(key1 = Unit) {
        Log.d("Quiz", "called")
        quizViewModel.selectedLevel = level
        Log.d("Quiz", "Level: " + quizViewModel.selectedLevel)
        quizViewModel.newQuestion()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("HandsOn")
                },
                actions = {

                    IconButton(onClick = { quizViewModel.switchLevel() }) {
                        Icon(
                            imageVector = Icons.Filled.TextIncrease,
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("${Screen.Level.route}/${quizViewModel.levelUnlocked}") }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized Description"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints() {
                Log.d("width", maxWidth.toString())

                //build layout differently, if the device is rotated
                val configuration = LocalConfiguration.current

                LaunchedEffect(configuration) {
                    snapshotFlow { configuration.orientation }
                        .collect { orientation = it }
                }

                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        LandscapeContent(quizViewModel = quizViewModel, modelReady)
                    }

                    else -> {
                        PortraitContent(quizViewModel = quizViewModel, modelReady)
                    }
                }
            }

        }
        LaunchedEffect(cameraPermissionState) {
            cameraPermissionState.launchPermissionRequest()
        }

        if (cameraPermissionState.status.isGranted) {
            Log.d("permission", "Camera permission Granted")
        } else {
            Column {
                val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission

                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required

                    fun onConfirmation() {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        ContextCompat.startActivity(context, intent, null)
                    }

                    fun onDismissRequest() {
                        activity?.finish()
                    }

                    AlertDialog(
                        onDismissRequest = { onDismissRequest() },
                        title = {
                            Text(text = "dialogTitle")
                        },
                        text = {
                            Text(text = "dialogText")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onConfirmation()
                                }
                            ) {
                                Text("Confirm")

                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    onDismissRequest()
                                }
                            ) {
                                Text("Dismiss")
                            }
                        }
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeContent(quizViewModel: QuizViewModel, modelReady: Boolean) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(Modifier.fillMaxWidth(0.5f)) {
            if (modelReady)
                Camera(
                    updateTranslation = { answer ->
                        checkAnswer(
                            quizViewModel = quizViewModel,
                            answer = answer
                        )
                    }, quizViewModel.mlModel!!)
        }


        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            TextField(
                value = quizViewModel.translation,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                maxLines = Int.MAX_VALUE,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { quizViewModel.skip() }) {
                    Text(text = "Skip")
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitContent(quizViewModel: QuizViewModel, modelReady: Boolean) {
    if (quizViewModel.showCorrect) {
        ShowCorrect()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),

        ) {
        Box(modifier = Modifier.fillMaxHeight(0.75f)) {

            if (modelReady) {
                Camera(
                    updateTranslation = { answer ->
                        checkAnswer(
                            quizViewModel = quizViewModel,
                            answer = answer
                        )
                    }, quizViewModel.mlModel!!)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = quizViewModel.translation,
            onValueChange = { quizViewModel.updateTranslation(it) },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            maxLines = Int.MAX_VALUE,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                quizViewModel.skip()
            }) {
                Text(text = "Skip")
            }
        }

    }
}

// takes the output of the ai-model and checks the answer
@SuppressLint("SuspiciousIndentation")
private fun checkAnswer(quizViewModel: QuizViewModel, answer: String, levelNotUnlocked: () -> Unit = {}) {
    if(quizViewModel.levelUnlocked < quizViewModel.selectedLevel) {
        levelNotUnlocked()
    }
    // ignored, if the "answer correct"-screen is currently displayed
    if (!quizViewModel.showCorrect) {
        quizViewModel.updateTranslation(
            question = quizViewModel.question,
            answer = answer
        )

        if (!quizViewModel.levelContainsWords()) {
            if (answer == quizViewModel.question) {
                quizViewModel.showCorrectAnswer(true)
                quizViewModel.newQuestion()
                if (quizViewModel.selectedLevel < quizViewModel.numberOfLevels)
                    Log.d("Quiz", "unlocked: ${quizViewModel.levelUnlocked}")
                    quizViewModel.levelUnlocked++
                Log.d("Quiz", "unlocked: ${quizViewModel.levelUnlocked}")
            }
        }
        else{
            // if the returned answer is equal to the letter of the current position in the question,
            // the letter is added to the answer and the answer-count is raised
            if (answer == quizViewModel.question[quizViewModel.answerCount].toString()) {
                quizViewModel.riseCounter()
                quizViewModel.answeredWord += answer
            }

            // if the answer is complete, the answer screen gets displayed and the question reset
            if (quizViewModel.question == quizViewModel.answeredWord) {
                quizViewModel.showCorrectAnswer(true)
                quizViewModel.newQuestion()
                quizViewModel.resetWord()
            }
        }


        /*
        // if the word-training-mode is not active
        if (!quizViewModel.levelTwo) {
            quizViewModel.updateTranslation(
                question = quizViewModel.questionLetter,
                answer = answer
            )

            if (answer == quizViewModel.questionLetter) {
                quizViewModel.showCorrectAnswer(true)
                quizViewModel.newQuestionLetter()
            }
        } else {
            quizViewModel.updateTranslation(
                question = quizViewModel.questionWord,
                answer = quizViewModel.answeredWord + answer
            )

            // if the returned answer is equal to the letter of the current position in the question,
            // the letter is added to the answer and the answer-count is raised
            if (answer == quizViewModel.questionWord[quizViewModel.answerCount].toString()) {
                quizViewModel.riseCounter()
                quizViewModel.answeredWord += answer
            }

            // if the answer is complete, the answer screen gets displayed and the question reset
            if (quizViewModel.questionWord == quizViewModel.answeredWord) {
                quizViewModel.showCorrectAnswer(true)
                quizViewModel.newQuestionWord()
                quizViewModel.resetWord()
            }
        }*/

    }
}

//show the "answer-correct"-screen
@Composable
private fun ShowCorrect(quizViewModel: QuizViewModel = viewModel()) {
    Dialog(onDismissRequest = { quizViewModel.showCorrectAnswer(false) }) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Localized Description",
            modifier = Modifier
                .height(150.dp)
                .width(150.dp),
            tint = Color.Green
        )

    }
}
