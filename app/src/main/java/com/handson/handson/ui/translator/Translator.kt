package com.handson.handson.ui.translator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.handson.handson.R
import com.handson.handson.ml.AslModel
import java.util.concurrent.Executors
import com.handson.handson.ui.Screen
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Translator(
    navController: NavController,
    translatorViewModel: TranslatorViewModel = viewModel()
) {

    val context = LocalContext.current
    val activity = (context as? Activity)
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
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Quiz.route) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
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
            BoxWithConstraints {
                if (maxWidth < 800.dp) {
                    if (translatorViewModel.showReverseTranslation) {
                        ReverseTranslation()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),

                        ) {
                        Box(modifier = Modifier.fillMaxHeight(0.75f)) {
                            Camera(translatorViewModel)
                        }


                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            value = translatorViewModel.translationText,
                            onValueChange = { translatorViewModel.updateTranslateText(it) },
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
                            Button(onClick = {
                                translatorViewModel.reverseTranslate()
                                translatorViewModel.showReverseTranslation(true)
                            }
                            ) {
                                Text(text = stringResource(R.string.reverse))
                            }
                            Button(onClick = { translatorViewModel.clearTranslationText() }) {
                                Text(text = stringResource(R.string.clear))
                            }
                        }

                    }

                } else {
                    if (translatorViewModel.showReverseTranslation) {
                        ReverseTranslation()
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Box(Modifier.fillMaxWidth(0.5f)) {
                            Camera()
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            TextField(
                                value = translatorViewModel.translationText,
                                onValueChange = { translatorViewModel.updateTranslateText(it) },
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
                                Button(onClick = {
                                    translatorViewModel.reverseTranslate()
                                    translatorViewModel.showReverseTranslation(true)
                                }) {
                                    Text(text = stringResource(R.string.reverse))
                                }
                                Button(onClick = { translatorViewModel.clearTranslationText() }) {
                                    Text(text = stringResource(R.string.clear))
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
                            Text(text = stringResource(R.string.camera_permission_required))
                        },
                        text = {
                            Text(text = stringResource(R.string.grant_camera_permission))
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onConfirmation()
                                }
                            ) {
                                Text(stringResource(R.string.grant_permission))

                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    onDismissRequest()
                                }
                            ) {
                                Text(stringResource(R.string.dismiss))
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
    translatorViewModel: TranslatorViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    val labels = listOf(
        "A", "B", "C", "D"
    )

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FIT_CENTER
            }

            val executor = Executors.newSingleThreadExecutor()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                        val model = AslModel.newInstance(
                            context,
                            Model.Options.Builder().setDevice(Model.Device.NNAPI).build()
                        )
                        // The image rotation and RGB image buffer are initialized only once
                        // the analyzer has started running
                        /*var bitmap: Bitmap = Bitmap.createBitmap(

                            imageProxy.width,
                            imageProxy.height,
                            Bitmap.Config.ARGB_8888
                        )*/


                        /* val plane = imageProxy.planes[0]
                         val imageBuffer = plane.buffer
                         val bytes = ByteArray(imageBuffer.remaining())
                         imageBuffer.get(bytes)

                         // Create a Bitmap from the image data
                         var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)*/
                        var bitmap = imageProxy.toBitmap()

                        bitmap = bitmap.scale(300, 300)
                        var buffer: ByteBuffer = ByteBuffer.allocate(300 * 300 * 3 * 4)
                        bitmap.copyPixelsToBuffer(buffer)

                        // Creates inputs for reference.
                        val inputFeature0 = TensorBuffer.createFixedSize(
                            intArrayOf(1, 300, 300, 3),
                            DataType.FLOAT32
                        )
                        inputFeature0.loadBuffer(buffer)

// Runs model inference and gets result.
                        val outputs = model.process(inputFeature0)
                        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                        val index = outputFeature0.floatArray.maxOfOrNull { it }
                            ?.let { outputFeature0.intArray.indexOf(it.toInt()) }

                        Log.d(
                            "output",
                            outputs.outputFeature0AsTensorBuffer.floatArray.contentToString()
                        )

                        val confidenceThreshold = 0.8 // Adjust the threshold as needed
                        if (index != null && outputFeature0.floatArray[index] > confidenceThreshold) {
                            val result = labels[index]
                            translatorViewModel.updateTranslation(result)
                        }

                        //TODO: Implement prediction through the tflite model

// Releases model resources if no longer used.
                        model.close()



                        imageProxy.close()

                    })
                }


            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)

                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            }, getMainExecutor(ctx))
            previewView
        })

}


@Composable
fun ReverseTranslation(translatorViewModel: TranslatorViewModel = viewModel()) {
    val reverseTranslatorImages = translatorViewModel.reverseTranslationImages
    Dialog(onDismissRequest = { translatorViewModel.showReverseTranslation(false) }) {
        Card(
            modifier = Modifier
                .height(300.dp)
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(R.string.reverse_translation),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(
                    CenterHorizontally
                )
            )

            Spacer(modifier = Modifier.height(10.dp))


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                items(reverseTranslatorImages.size) { index ->

                    val painter =
                        painterResource(id = reverseTranslatorImages[index].imageResourceId)

                    Image(
                        painter = painter,
                        contentDescription = null, // Set a meaningful content description if needed
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

