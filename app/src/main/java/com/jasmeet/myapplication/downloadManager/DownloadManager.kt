package com.jasmeet.myapplication.downloadManager

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManagerClass(
    private val context: Context
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager

    suspend fun startDownload(
        downloadUrl: String,
        fileName: String,
    ): Long = withContext(Dispatchers.IO) {
        val request = DownloadManager.Request(downloadUrl.toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(true)
            .setTitle("Downloading File")
            .setDescription("Downloading a file from the internet.")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                "video.mp4"
            )

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    suspend fun getDownloadStatus(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

        if (cursor.moveToFirst()) {
            if (statusIndex != -1){
                val status = cursor.getInt(statusIndex)
                cursor.close()
                return status
            }
        }
        cursor.close()
        return -1
    }

    suspend fun getDownloadedFilePath(downloadId: Long): String {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        val filePathIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)
        if (cursor.moveToFirst()) {
            if (filePathIndex != -1){
                val filePath = cursor.getString(filePathIndex)
                cursor.close()
                return filePath
            }
        }
        cursor.close()
        return ""
    }

    suspend fun getDownloadProgress(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val downloadedBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

            if (downloadedBytesIndex != -1 && totalBytesIndex != -1) {
                val downloadedBytes = cursor.getInt(downloadedBytesIndex)
                val totalBytes = cursor.getInt(totalBytesIndex)
                cursor.close()

                if (totalBytes > 0) {
                    return (downloadedBytes * 100L / totalBytes).toInt()
                }
            }
        }

        cursor.close()
        return 0
    }

    //this function will cancel the download
    suspend fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }

    //this function will handle the error
    suspend fun handleError(downloadId: Long):String {
        when (val status =getDownloadStatus(downloadId)) {
            DownloadManager.STATUS_FAILED -> {
                return "Download Failed"
            }
            DownloadManager.STATUS_PAUSED -> {
                return "Download Paused"
            }
            DownloadManager.STATUS_PENDING -> {
                return "Download Pending"
            }
            DownloadManager.STATUS_RUNNING -> {
                return "Download Running"
            }
            DownloadManager.STATUS_SUCCESSFUL -> {
                return "Download Successful"
            }
            else -> {
                return "Download Failed"
            }
        }
    }

    fun getDownloadedBytes(value: Long): Long {

        val query = DownloadManager.Query().setFilterById(value)
        val cursor = downloadManager.query(query)
        val downloadedBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        if (cursor.moveToFirst()) {
            if (downloadedBytesIndex != -1){
                val downloadedBytes = cursor.getLong(downloadedBytesIndex)
                cursor.close()
                return downloadedBytes
            }
        }
        cursor.close()
        return 0
    }

    fun getFileSizeMB(value: Long): Long {
        val query = DownloadManager.Query().setFilterById(value)
        val cursor = downloadManager.query(query)
        val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        if (cursor.moveToFirst()) {
            if (totalBytesIndex != -1){
                val totalBytes = cursor.getLong(totalBytesIndex)
                cursor.close()
                return totalBytes / (1024 * 1024)
            }
        }
        cursor.close()
        return 0
    }


}