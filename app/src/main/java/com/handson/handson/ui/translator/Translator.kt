package com.handson.handson.ui.translator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.util.concurrent.Executors


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Translator(translatorViewModel: TranslatorViewModel = viewModel()) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = (context as? Activity)

    val cameraController = remember { LifecycleCameraController(context) }

    var text by rememberSaveable { mutableStateOf("") }


    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("HandsOn")
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
                if (maxWidth < 800.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),

                        ) {
                        Box(modifier = Modifier.fillMaxHeight(0.75f)) {
                            Camera(cameraController, lifecycleOwner)
                        }


                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            value = translatorViewModel.translation, onValueChange = { text = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.6f),
                            maxLines = Int.MAX_VALUE
                        )


                        Spacer(modifier = Modifier.height(15.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "Reverse")
                            }
                            Button(onClick = { /*TODO*/ }) {
                                Text(text = "Clear")
                            }
                        }

                    }

                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(Modifier.fillMaxWidth(0.5f)) {
                            Camera(cameraController, lifecycleOwner)
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            TextField(
                                value = translatorViewModel.translation,
                                onValueChange = { text = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.7f),
                                maxLines = Int.MAX_VALUE
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(onClick = { /*TODO*/ }) {
                                    Text(text = "Reverse")
                                }
                                Button(onClick = { /*TODO*/ }) {
                                    Text(text = "Clear")
                                }
                            }

                        }
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
                        startActivity(context, intent, null)
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

@Composable
private fun Camera(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner
) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { context ->
            PreviewView(context).apply {
                layoutParams =
                    LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FIT_CENTER
            }.also { previewView ->
                previewView.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)

                val imageAnalysis = ImageAnalysis.Builder()
                    // enable the following line if RGBA output is needed.
                    // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val executor = Executors.newFixedThreadPool(5)
                imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy ->
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    // insert your code here.

                    // after done, release the ImageProxy object
                    imageProxy.close()
                })

            }
        })
}



