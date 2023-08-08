package com.jasmeet.myapplication

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jasmeet.myapplication.ui.theme.MyApplicationTheme
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

    var downloadProgress by remember { mutableStateOf(0.0f) }
    var showProgressBar by remember {
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
    var downloadedBytes by remember { mutableStateOf(0L) }

    var fileSizeMB by remember { mutableStateOf(0L) }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val downloadId2 = remember {
        mutableStateOf(0L)

    }


    val animatedProgress by animateFloatAsState(
        targetValue = downloadProgress,
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
                        downloadId2.value = downloadFile(
                            context,
                            downloadManager,
                            "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4"
                        )
                        showProgressBar = true


                    }
                }
            ) {
                Text(text = "Download")
            }
            Button(
                onClick = {
                    scope.launch {
                        downloadManager.remove(downloadId2.value)
                        showProgressBar = false
                        downloadProgress = 0.0f
                        downloadId2.value = 0L
                        downloadedBytes = 0L
                        fileSizeMB = 0L

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

        }


        if (downloadId2.value == 1L) {
            Text(text = "Download Completed")
            isDownloadTextVisible.value = false
            isFileDownloaded.value = true
        }
        if(showProgressBar){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            isDownloadTextVisible.value = true
            isFileDownloaded.value = false
        }
        if (isDownloadTextVisible.value) {
            if (downloadedBytes > 0 && fileSizeMB > 0) {
                val downloadedMB = downloadedBytes / (1024 * 1024)
                Text(text = "${downloadedMB}MB/${fileSizeMB}MB")
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
                        showProgressBar = false
                        downloadedBytes = cursor.getLong(totalBytesIndex)
                        downloadProgress = 1.0f
                        showToast(context, "Download Completed")
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Download Failed")
                    }else if (status == DownloadManager.ERROR_FILE_ALREADY_EXISTS){
                        downloadId2.value= 1L
                        showProgressBar = false
                        downloadedBytes = cursor.getLong(totalBytesIndex)
                        downloadProgress = 1.0f
                        showToast(context, "File Already Exists")
                    }
                    else if(status ==DownloadManager.ERROR_FILE_ERROR){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "File Error")
                    }
                    else if(status == DownloadManager.ERROR_HTTP_DATA_ERROR){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "HTTP Data Error")
                    }
                    else if(status == DownloadManager.ERROR_INSUFFICIENT_SPACE){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Insufficient Space")
                    }
                    else if(status == DownloadManager.ERROR_TOO_MANY_REDIRECTS){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Too Many Redirects")
                    }
                    else if(status == DownloadManager.ERROR_UNHANDLED_HTTP_CODE){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Unhandled HTTP Code")
                    }
                    else if(status == DownloadManager.ERROR_UNKNOWN){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Unknown Error")
                    }
                    else if(status == DownloadManager.PAUSED_WAITING_FOR_NETWORK){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Paused Waiting For Network")
                    }
                    else if(status == DownloadManager.PAUSED_WAITING_TO_RETRY){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Paused Waiting To Retry")
                    }
                    else if(status == DownloadManager.STATUS_PAUSED){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Paused")
                    }
                    else if(status == DownloadManager.STATUS_PENDING){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Pending")
                    }
                    else if(status == DownloadManager.STATUS_RUNNING){
                        downloadId2.value= 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                        showToast(context, "Running")

                    }
                    else {
                        val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                        val totalBytes = cursor.getLong(totalBytesIndex)
                        if (totalBytes > 0) {
                            showProgressBar = true
                            downloadProgress = bytesDownloaded.toFloat()/ totalBytes.toFloat()
                            fileSizeMB = totalBytes / (1024 * 1024)
                            downloadedBytes = bytesDownloaded
                        }
                    }
                }
            }
            cursor.close()
            delay(50)
        }
    }
}

fun downloadFile(context: Context, downloadManager: DownloadManager,url : String):Long {
    return try {
        val request = DownloadManager.Request(url.toUri())
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "video.mp4")

        val downloadId = downloadManager.enqueue(request)
        downloadId
    }

    catch (e: Exception) {
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        Log.d("Ecxeption", "downloadFile: ${e.message}")
        0L
    }

}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


//cancellation button