package com.jasmeet.myapplication.fileDownloader

import android.app.DownloadManager
import android.os.Environment
import androidx.core.net.toUri

object FileDownloadObject {
    fun downloadFile(downloadManager: DownloadManager, url: String): Long {

        val request = DownloadManager.Request(url.toUri())
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "video.mp4")

        return downloadManager.enqueue(request)


    }
}