package com.jasmeet.myapplication

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import com.jasmeet.myapplication.fileDownloader.FileDownloadObject
import com.jasmeet.myapplication.ui.theme.MyApplicationTheme
import com.jasmeet.myapplication.utils.Utils.showToast
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

                    DownloadSection33(this)

                }
            }
        }
    }

}



@Composable
fun DownloadSection33(context: Context) {

    val downloadProgress = remember { mutableStateOf(0.0f) }
    val showProgressBar = remember {
        mutableStateOf(
            false
        )
    }
    val isDownloadTextVisible = remember {
        mutableStateOf(
            false
        )
    }
    val isFileDownloaded = remember {
        mutableStateOf(
            false
        )
    }
    val downloadedBytes = remember { mutableStateOf(0L) }

    val fileSizeMB = remember { mutableStateOf(0L) }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val downloadId2 = remember {
        mutableStateOf(0L)

    }


    val animatedProgress by animateFloatAsState(
        targetValue = downloadProgress.value,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )

    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(progress = animatedProgress, color = Color.Red, trackColor = Color.Yellow)

        Spacer(modifier = Modifier.padding(top = 18.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    scope.launch {
                        downloadId2.value = FileDownloadObject.downloadFile(
                            downloadManager,
                            "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4"
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
                        downloadManager.remove(downloadId2.value)
                        showProgressBar.value = false
                        downloadProgress.value = 0.0f
                        downloadId2.value = 0L
                        downloadedBytes.value = 0L
                        fileSizeMB.value = 0L

                        //also delete the file from the storage
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
//            // a button for pause and resume THE DOWNLOAD
//            Button(
//                onClick = {
//                    scope.launch {
//                        downloadManager.openDownloadedFile(downloadId2.value)
//                    }
//                },
//                enabled = isFileDownloaded.value
//            ) {
//                Text(text = "Open")
//            }

        }


        if (downloadId2.value == 1L) {
            Text(text = "Download Completed")
            isDownloadTextVisible.value = false
            isFileDownloaded.value = true
        }
        if(showProgressBar.value){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            isDownloadTextVisible.value = true
            isFileDownloaded.value = false
        }
        if (isDownloadTextVisible.value) {
            if (downloadedBytes.value > 0 && fileSizeMB.value > 0) {
                val downloadedMB = downloadedBytes.value / (1024 * 1024)
                val text = "${downloadedMB}MB/${fileSizeMB.value}MB"
                Text(text = text)
                Log.d("TAGO", "DownloadSection33: $text")
                isFileDownloaded.value = false
            }
        }
    }
    LaunchedEffect(downloadId2.value) {
        while (downloadId2.value != 0L) {
            val query = DownloadManager.Query().setFilterById(downloadId2.value)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val bytesDownloadedIndex =
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                if (statusIndex != -1 && bytesDownloadedIndex != -1 && totalBytesIndex != -1) {
                    val status = cursor.getInt(statusIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadId2.value= 1L
                        showProgressBar.value = false
                        downloadedBytes.value = cursor.getLong(totalBytesIndex)
                        downloadProgress .value= 1.0f
                        showToast(context, "Download Completed")
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value= false
                        showToast(context, "Download Failed")
                    }else if (status == DownloadManager.ERROR_FILE_ALREADY_EXISTS){
                        downloadId2.value= 1L
                        showProgressBar.value = false
                        downloadedBytes.value = cursor.getLong(totalBytesIndex)
                        downloadProgress.value= 1.0f
                        showToast(context, "File Already Exists")
                    }
                    else if(status ==DownloadManager.ERROR_FILE_ERROR){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "File Error")
                    }
                    else if(status == DownloadManager.ERROR_HTTP_DATA_ERROR){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "HTTP Data Error")
                    }
                    else if(status == DownloadManager.ERROR_INSUFFICIENT_SPACE){
                        downloadId2.value = 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "Insufficient Space")
                    }
                    else if(status == DownloadManager.ERROR_TOO_MANY_REDIRECTS){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "Too Many Redirects")
                    }
                    else if(status == DownloadManager.ERROR_UNHANDLED_HTTP_CODE){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "Unhandled HTTP Code")
                    }
                    else if(status == DownloadManager.ERROR_UNKNOWN){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "Unknown Error")
                    }
                    else if(status == DownloadManager.PAUSED_WAITING_TO_RETRY){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value = false
                        showToast(context, "Paused Waiting To Retry")
                    }
                    else if(status == DownloadManager.STATUS_PAUSED){
                        downloadId2.value= 0L
                        downloadProgress.value = 0.0f
                        showProgressBar.value= false
                        showToast(context, "Paused")
                    }

                    else {
                        val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                        val totalBytes = cursor.getLong(totalBytesIndex)
                        if (totalBytes > 0) {
                            showProgressBar.value = true
                            downloadProgress.value = bytesDownloaded.toFloat()/ totalBytes.toFloat()
                            fileSizeMB.value = totalBytes / (1024 * 1024)
                            downloadedBytes.value = bytesDownloaded
                        }
                    }
                }
            }
            cursor.close()
            delay(50)
        }
    }
}





//cancellation button