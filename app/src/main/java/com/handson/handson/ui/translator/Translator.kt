package com.handson.handson.ui.translator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.Hands
import com.google.mediapipe.solutions.hands.HandsOptions
import com.handson.handson.R
import com.handson.handson.ml.AslModel
import com.handson.handson.ui.Screen
import kotlinx.coroutines.delay
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.model.Model
import java.util.concurrent.Executors


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
                            Camera() { result ->
                                translatorViewModel.updateTranslation(
                                    result
                                )
                            }
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
                            Camera() { result ->
                                translatorViewModel.updateTranslation(
                                    result
                                )
                            }
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

fun cropImageWithBoundingBox(originalBitmap: Bitmap, boundingBox: RectF): Bitmap {
    val left = (boundingBox.left * originalBitmap.width).toInt()
    val top = (boundingBox.top * originalBitmap.height).toInt()
    val right = (boundingBox.right * originalBitmap.width).toInt()
    val bottom = (boundingBox.bottom * originalBitmap.height).toInt()

    // Ensure the coordinates are within the bounds of the original image
    val clampedLeft = left.coerceIn(0, originalBitmap.width - 1)
    val clampedTop = top.coerceIn(0, originalBitmap.height - 1)
    val clampedRight = right.coerceIn(0, originalBitmap.width - 1)
    val clampedBottom = bottom.coerceIn(0, originalBitmap.height - 1)

    // Calculate the width and height of the cropped region
    val width = clampedRight - clampedLeft
    val height = clampedBottom - clampedTop

    // Create a new Bitmap that represents the cropped region
    val croppedBitmap = Bitmap.createBitmap(width, height, originalBitmap.config)

    // Copy the pixels from the original image to the cropped image
    val canvas = Canvas(croppedBitmap)
    val srcRect = Rect(clampedLeft, clampedTop, clampedRight, clampedBottom)
    val destRect = Rect(0, 0, width, height)
    canvas.drawBitmap(originalBitmap, srcRect, destRect, null)

    return croppedBitmap
}

fun calculateHandBoundingBox(landmarks: List<LandmarkProto.NormalizedLandmark>): RectF {
    // Initialize variables to find the bounding box coordinates
    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE
    var padding = 0.1f

    for (landmark in landmarks) {
        val x = landmark.x
        val y = landmark.y

        // Update the min and max coordinates
        if (x < minX) {
            minX = x
        }
        if (x > maxX) {
            maxX = x
        }
        if (y < minY) {
            minY = y
        }
        if (y > maxY) {
            maxY = y
        }
    }

    // Create a RectF with the calculated bounding box coordinates
    return RectF(minX - padding, minY - padding, maxX + padding, maxY + padding)
}


@Composable
fun Camera(updateTranslation: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

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
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .apply {
                    val model = AslModel.newInstance(
                        context,
                        Model.Options.Builder().setDevice(Model.Device.NNAPI).setNumThreads(8)
                            .build()
                    )

                    val handsOptions = HandsOptions.builder()
                        .setModelComplexity(1)
                        .setMaxNumHands(1)
                        .setRunOnGpu(true)
                        .setStaticImageMode(false)
                        .setMinTrackingConfidence(0.9f)
                        .build()
                    val hands = Hands(context, handsOptions)

                    setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy: ImageProxy ->


                        hands.setResultListener { result ->
                            val multiLandmarks = result.multiHandLandmarks()
                            var boundingBox: RectF = RectF()

                            if (multiLandmarks != null && multiLandmarks.isNotEmpty()) {
                                for (i in 0 until multiLandmarks.size) {
                                    val landmarks = multiLandmarks[i]

                                    // Extract individual landmarks from the NormalizedLandmarkList
                                    val normalizedLandmarks = landmarks.landmarkList

                                    // Calculate the bounding box for each detected hand
                                    boundingBox = calculateHandBoundingBox(normalizedLandmarks)
                                }

                                Log.d("hands", boundingBox.toString())
                                val croppedBitmap =
                                    cropImageWithBoundingBox(result.inputBitmap(), boundingBox)

                                var bitmap = croppedBitmap

                                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
                                /*
                                   var buffer: ByteBuffer = ByteBuffer.allocate(300 * 300 * 3 * 4)
                                  bitmap.copyPixelsToBuffer(buffer)*/

                                var tImage: TensorImage = TensorImage(DataType.FLOAT32)
                                tImage.load(bitmap)

                                // only for testing purposes
                                // NormalizeOp(0f,255.0f).apply(tImage.tensorBuffer)

                                var processor = ImageProcessor.Builder()
                                    .add(Rot90Op())
                                    .add(Rot90Op())
                                    .add(Rot90Op())
                                    .build()
                                processor.process(tImage)

                                //  translatorViewModel.setBitmap(tImage.bitmap)


                                // Runs model inference and gets result.
                                val outputs = model.process(
                                    tImage.tensorBuffer
                                )


                                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                                val index = outputFeature0.floatArray.maxOfOrNull { it }
                                    ?.let { outputFeature0.intArray.indexOf(it.toInt()) }

                                Log.d(
                                    "output",
                                    outputs.outputFeature0AsTensorBuffer.floatArray.contentToString()
                                )
                                Log.d(
                                    "output",
                                    outputFeature0.floatArray.maxOfOrNull { it }.toString()
                                )

                                val confidenceThreshold = 0.75 // Adjust the threshold as needed
                                if (index != null && outputFeature0.floatArray[index] > confidenceThreshold) {
                                    val result = labels[index]
                                    updateTranslation(result)
                                }


                            }
                        }

                        hands.send(imageProxy.toBitmap(), System.currentTimeMillis())

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


                        // Releases model resources if no longer used.


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
    val lazyColumnListState = rememberLazyListState()
    var isAnimating by remember { mutableStateOf(true) }
    var isDoubleSpeed by remember { mutableStateOf(false) }
    var isStopped by remember { mutableStateOf(false) }
    var itemDurationMillis = 2000L // Adjust the duration for each item on screen

    // Animate the Reverse translation (auto slide to next image)
    LaunchedEffect(key1 = isAnimating, key2 = isDoubleSpeed) {

        if (isDoubleSpeed) {
            itemDurationMillis /= 4
        }

        if (isAnimating) {
            if (isStopped || lazyColumnListState.firstVisibleItemIndex == reverseTranslatorImages.size - 1) {
                lazyColumnListState.scrollToItem(0)
                isStopped = false
            }
            for (index in lazyColumnListState.firstVisibleItemIndex until reverseTranslatorImages.size) {
                lazyColumnListState.animateScrollToItem(index)
                delay(itemDurationMillis)
            }
            isAnimating = false
        }

    }


    Dialog(onDismissRequest = { translatorViewModel.showReverseTranslation(false) }) {
        Card(
            modifier = Modifier
                .height(400.dp)
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(R.string.reverse_translation),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(
                        CenterHorizontally
                    )
                    .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .align(CenterHorizontally)
            ) {

                if (!isAnimating) {
                    FilledIconButton(onClick = { isAnimating = true }) {
                        Icon(Icons.Outlined.PlayArrow, "Play Animation")

                    }
                } else {
                    FilledIconButton(onClick = {
                        isStopped = true
                        isAnimating = false
                    }) {
                        Icon(Icons.Outlined.Stop, "Stop Animation")
                    }
                }
                if (!isAnimating) {
                    OutlinedIconButton(onClick = { isAnimating = false }, enabled = false) {
                        Icon(Icons.Outlined.Pause, "Pause Animation")

                    }
                } else {
                    OutlinedIconButton(onClick = { isAnimating = false }) {
                        Icon(Icons.Outlined.Pause, "Pause Animation")

                    }
                }


                OutlinedIconToggleButton(
                    checked = isDoubleSpeed,
                    onCheckedChange = { newChecked -> isDoubleSpeed = !isDoubleSpeed }) {
                    Icon(Icons.Outlined.Speed, "Fast Animation")


                }

            }



            LazyColumn(
                state = lazyColumnListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                items(reverseTranslatorImages.size) { index ->

                    val painter =
                        painterResource(id = reverseTranslatorImages[index].imageResourceId)

                    Image(
                        painter = painter,
                        contentScale = ContentScale.Inside,
                        contentDescription = null, // Set a meaningful content description if needed
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    /*Image(
                        bitmap = translatorViewModel.testBitmap.asImageBitmap(),
                        contentDescription = "test"
                    )*/
                }

            }
        }
    }
}


