package com.jasmeet.myapplication

import android.app.DownloadManager
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jasmeet.myapplication.downloadManager.DownloadManagerClass
import com.jasmeet.myapplication.ui.theme.MyApplicationTheme
import com.jasmeet.myapplication.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    DownloadScreen()

                }
            }
        }
    }

}

//@Composable
//fun DownloadSection33(context: Context) {
//    val downloadProgress = remember { mutableStateOf(0.0f) }
//    val downloadedBytes = remember { mutableStateOf(0L) }
//    val fileSizeMB = remember { mutableStateOf(0L) }
//
//    val showProgressBar = remember { mutableStateOf(false) }
//
//    val animatedProgress by animateFloatAsState(
//        targetValue = downloadProgress.value,
//        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
//    )
//
//    val downloadManagerClass = DownloadManagerClass(context)
//    val scope = rememberCoroutineScope()
//
//    val downloadId2 = remember { mutableStateOf(0L) }
//    val isFileDownloaded = remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        LinearProgressIndicator(
//            progress = animatedProgress,
//            color = Color.Red,
//            trackColor = Color.Yellow
//        )
//
//        Spacer(modifier = Modifier.padding(top = 18.dp))
//
//        Row(
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Button(
//                onClick = {
//                    scope.launch {
//                        val fileName = "videos.mp4"
//                        downloadId2.value = downloadManagerClass.startDownload(
//                            "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4",
//                            fileName
//                        )
//                        showProgressBar.value = true
//                    }
//                }
//            ) {
//                Text(text = "Download")
//            }
//            Button(
//                onClick = {
//                    scope.launch {
//                        downloadManagerClass.cancelDownload(downloadId2.value)
//                        showProgressBar.value = false
//                        downloadProgress.value = 0.0f
//                        downloadId2.value = 0L
//                        downloadedBytes.value = 0L
//                        fileSizeMB.value = 0L
//
//                        // Delete the downloaded file from storage
//                        val file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//                        file?.listFiles()?.forEach {
//                            if (it.name == "video.mp4") {
//                                it.delete()
//                            }
//                        }
//                    }
//                },
//                enabled = !isFileDownloaded.value
//            ) {
//                Text(text = "Cancel")
//            }
//        }
//
//        if (downloadId2.value == 1L) {
//            Text(text = "Download Completed")
//            isFileDownloaded.value = true
//        }
//        if (showProgressBar.value) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//        }
//        if (downloadedBytes.value > 0 && fileSizeMB.value > 0) {
//            val downloadedMB = downloadedBytes.value / (1024 * 1024)
//            val text = "${downloadedMB}MB/${fileSizeMB.value}MB"
//            Text(text = text)
//        }
//    }
//
//    LaunchedEffect(downloadId2.value) {
//        while (downloadId2.value != 0L) {
//            when (downloadManagerClass.getDownloadStatus(downloadId2.value)) {
//                DownloadManager.STATUS_SUCCESSFUL -> {
//                    showProgressBar.value = false
//                    downloadedBytes.value = downloadManagerClass.getDownloadedBytes(downloadId2.value)
//                    downloadProgress.value = 1.0f
//                    Utils.showToast(context, "Download Completed")
//                }
//                DownloadManager.STATUS_FAILED -> {
//                    downloadProgress.value = 0.0f
//                    showProgressBar.value = false
//                    Utils.showToast(context, "Download Failed")
//                }
//                else -> {
//                    val progress = downloadManagerClass.getDownloadProgress(downloadId2.value)
//                    if (progress > 0) {
//                        showProgressBar.value = true
//                        downloadProgress.value = progress / 100f
//                        fileSizeMB.value = downloadManagerClass.getFileSizeMB(downloadId2.value)
//                        downloadedBytes.value = downloadManagerClass.getDownloadedBytes(downloadId2.value)
//                    }
//                }
//            }
//            delay(50)
//        }
//    }
//
//
//}


@Composable
fun DownloadSection3() {
    val context = LocalContext.current
    val downloadProgress = remember { mutableStateOf(0.0f) }
    val downloadedBytes = remember { mutableStateOf(0L) }
    val fileSizeMB = remember { mutableStateOf(0L) }

    val showProgressBar = remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = downloadProgress.value,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy), label = ""
    )

    val singleDownloadUrlList = listOf(
        "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4",
        "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4",
        "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4"
    )
    val fileNames = listOf(
        "video1.mp4",
        "video2.mp4",
        "video3.mp4"
    )

    val scope = rememberCoroutineScope()

    val downloadId2 = remember { mutableStateOf(emptyList<Long>()) }
    val isFileDownloaded = remember { mutableStateOf(false) }


    val downloadManagerClass = remember { DownloadManagerClass(context) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = animatedProgress,
            color = Color.Red,
            trackColor = Color.Yellow,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    scope.launch {
                        downloadId2.value = downloadManagerClass.startDownloads(
                            singleDownloadUrlList,
                            fileNames,
                            false
                        )
                    }
                }
            ) {
                Text(text = "Download")
            }

            Button(
                onClick = {
                    if (downloadId2.value.isNotEmpty()) {

                        downloadManagerClass.cancelDownloads(downloadId2.value)
                        showProgressBar.value = false
                        downloadProgress.value = 0.0f
                        downloadId2.value = emptyList()
                        downloadedBytes.value = 0L
                        fileSizeMB.value = 0L

                        // Delete the downloaded file from storage
                        val file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        file?.listFiles()?.forEach {
                            if (it.name == "noob.mp4") {
                                it.delete()
                            }
                        }
                    }
                },
                enabled = !isFileDownloaded.value
            ) {
                Text(text = "Cancel")
            }
        }

        if (downloadId2.value.isNotEmpty()) {
            Text(text = "Download Completed")
            isFileDownloaded.value = true
        }
        if (showProgressBar.value) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        if (downloadedBytes.value > 0 && fileSizeMB.value > 0) {
            val downloadedMB = downloadedBytes.value / (1024 * 1024)
            val text = "${downloadedMB}MB/${fileSizeMB.value}MB"
            Text(text = text)
        }

    }

    LaunchedEffect(downloadId2.value) {
        while (downloadId2.value.isNotEmpty()) {
            for (downloadId in downloadId2.value) {
                when (downloadManagerClass.getCombinedDownloadStatus(listOf(downloadId))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        showProgressBar.value = false
                        downloadedBytes.value = downloadManagerClass.getTotalDownloadedBytes(listOf(downloadId))
                        downloadProgress.value = 1.0f
                        Utils.showToast(context, "Download Completed")
                        downloadId2.value = emptyList()
                    }
                    DownloadManager.STATUS_FAILED -> {
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        Utils.showToast(context, "Download Failed")
                        downloadId2.value = emptyList()
                    }
                    else -> {
                        downloadProgress.value = downloadManagerClass.getDownloadProgress(listOf(downloadId)).toFloat()
                        if (downloadProgress.value > 0) {
                            showProgressBar.value = true
                            downloadProgress.value = downloadProgress.value / 100f
                            fileSizeMB.value = downloadManagerClass.getTotalFilesSizeMB(listOf(downloadId))
                            downloadedBytes.value = downloadManagerClass.getTotalDownloadedBytes(listOf(downloadId))
                        }
                    }
                }
            }
            delay(50) // Delay to avoid blocking the thread
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen() {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Download Section") }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                DownloadSection3()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        DownloadScreen()
    }
}







