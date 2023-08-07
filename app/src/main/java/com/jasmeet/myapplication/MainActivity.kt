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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.jasmeet.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

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
    var downloadId by remember { mutableStateOf(0L) }
    var downloadProgress by remember { mutableStateOf(0.0f) }
    var showProgressBar by remember {
        mutableStateOf(
            false
        )
    }

    val animatedProgress by animateFloatAsState(
        targetValue = downloadProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(progress = animatedProgress, color = Color.Red, trackColor = Color.Yellow)

        Button(
            onClick = {
                try{
                    val request = DownloadManager.Request("https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-Video-File-For-Testing.mp4".toUri())
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "video.mp4")

                    downloadId = downloadManager.enqueue(request)
                    Log.d("TAGH", "DownloadSection:  {$downloadProgress}")
                }catch (e:Exception){
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("TAGH", "DownloadSection:  ${e.message.toString()}")
                }

            }
        ) {
            Text(text = "Download")
        }

        if (downloadId == 1L) {
            Text(text = "Download Completed")
        }

        if(showProgressBar){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
    LaunchedEffect(downloadId) {
        while (downloadId != 0L) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val bytesDownloadedIndex =
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                if (statusIndex != -1 && bytesDownloadedIndex != -1 && totalBytesIndex != -1) {
                    val status = cursor.getInt(statusIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadId = 1L
                        showProgressBar = false
                        downloadProgress = 1.0f
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloadId = 0L
                        downloadProgress = 0.0f
                        showProgressBar= false
                    } else {
                        val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                        val totalBytes = cursor.getLong(totalBytesIndex)
                        if (totalBytes > 0) {
                            showProgressBar = true
                            downloadProgress = bytesDownloaded.toFloat() / totalBytes.toFloat()
                        }
                    }
                }
            }
            cursor.close()
            delay(50) // Polling interval
            Log.d("TAGH1", "DownloadSection:  {$downloadProgress}")
        }

    }
}
