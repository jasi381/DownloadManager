package com.jasmeet.myapplication

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

                    DownloadSection3(this)

                }
            }
        }
    }

}

@Composable
fun DownloadSection3(context: Context) {
    val downloadProgress = remember { mutableStateOf(0.0f) }
    val downloadedBytes = remember { mutableStateOf(0L) }
    val fileSizeMB = remember { mutableStateOf(0L) }

    val showProgressBar = remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = downloadProgress.value,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )

    val downloadManagerClass = DownloadManagerClass(context)
    val scope = rememberCoroutineScope()

    val downloadId2 = remember { mutableStateOf(0L) }
    val isFileDownloaded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = animatedProgress,
            color = Color.Red,
            trackColor = Color.Yellow
        )

        Spacer(modifier = Modifier.padding(top = 18.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    scope.launch {
                        val fileName = "videos.mp4"
                        downloadId2.value = downloadManagerClass.startDownload(
                            "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4",
                            fileName
                        )
                        showProgressBar.value = true
                    }
                }
            ) {
                Text(text = "Download")
            }
            Button(
                onClick = {
                    scope.launch {
                        downloadManagerClass.cancelDownload(downloadId2.value)
                        showProgressBar.value = false
                        downloadProgress.value = 0.0f
                        downloadId2.value = 0L
                        downloadedBytes.value = 0L
                        fileSizeMB.value = 0L

                        // Delete the downloaded file from storage
                        val file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        file?.listFiles()?.forEach {
                            if (it.name == "video.mp4") {
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

        if (downloadId2.value == 1L) {
            Text(text = "Download Completed")
            isFileDownloaded.value = true
        }
        if (showProgressBar.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        if (downloadedBytes.value > 0 && fileSizeMB.value > 0) {
            val downloadedMB = downloadedBytes.value / (1024 * 1024)
            val text = "${downloadedMB}MB/${fileSizeMB.value}MB"
            Text(text = text)
        }
    }

    LaunchedEffect(downloadId2.value) {
        while (downloadId2.value != 0L) {
            when (downloadManagerClass.getDownloadStatus(downloadId2.value)) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    downloadId2.value = 1L
                    showProgressBar.value = false
                    downloadedBytes.value = downloadManagerClass.getDownloadedBytes(downloadId2.value)
                    downloadProgress.value = 1.0f
                    Utils.showToast(context, "Download Completed")
                }
                DownloadManager.STATUS_FAILED -> {
                    downloadId2.value = 0L
                    downloadProgress.value = 0.0f
                    showProgressBar.value = false
                    Utils.showToast(context, "Download Failed")
                }
                else -> {
                    val progress = downloadManagerClass.getDownloadProgress(downloadId2.value)
                    if (progress > 0) {
                        showProgressBar.value = true
                        downloadProgress.value = progress / 100f
                        fileSizeMB.value = downloadManagerClass.getFileSizeMB(downloadId2.value)
                        downloadedBytes.value = downloadManagerClass.getDownloadedBytes(downloadId2.value)
                    }
                }
            }
            delay(50)
        }
    }
}







