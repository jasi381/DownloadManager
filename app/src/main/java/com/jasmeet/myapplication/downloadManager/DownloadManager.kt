package com.jasmeet.myapplication.downloadManager

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManagerClass(
    private val context: Context
) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    suspend fun startDownloads(
        downloadUrls: List<String>,
        fileNames: List<String>,
        downloadOnlyOne: Boolean = false
    ): List<Long> = withContext(Dispatchers.IO) {
        val downloadIds = mutableListOf<Long>()
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        if (downloadOnlyOne && downloadUrls.isNotEmpty()) {
            val request = DownloadManager.Request(downloadUrls[0].toUri())
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setTitle("Downloading File")
                .setDescription("Downloading a file from the internet.")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "${fileNames[0]}.mp4"
                )


            val downloadId = downloadManager.enqueue(request)
            downloadIds.add(downloadId)
        } else {
            for (i in downloadUrls.indices) {
                val request = DownloadManager.Request(downloadUrls[i].toUri())
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true)
                    .setTitle("Downloading File")
                    .setDescription("Downloading a file from the internet.")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "${fileNames[i]}.mp4"
                    )

                val downloadId = downloadManager.enqueue(request)
                downloadIds.add(downloadId)
            }
        }

        return@withContext downloadIds
    }

    fun getCombinedDownloadStatus(downloadIds: List<Long>): Int {
        var combinedStatus = DownloadManager.STATUS_SUCCESSFUL

        for (downloadId in downloadIds) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

            if (cursor.moveToFirst()) {
                if (statusIndex != -1) {
                    val status = cursor.getInt(statusIndex)
                    if (status != DownloadManager.STATUS_SUCCESSFUL) {
                        combinedStatus = status
                    }
                }
            }
            cursor.close()
        }

        return combinedStatus
    }



    fun cancelDownloads(downloadIds: List<Long>) {
        for (downloadId in downloadIds) {
            downloadManager.remove(downloadId)
        }
    }


    fun getTotalDownloadedBytes(downloadIds: List<Long>): Long {
        var totalDownloadedBytes = 0L

        for (downloadId in downloadIds) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            val downloadedBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

            if (cursor.moveToFirst()) {
                if (downloadedBytesIndex != -1) {
                    val downloadedBytesValue = cursor.getLong(downloadedBytesIndex)
                    totalDownloadedBytes += downloadedBytesValue
                }
            }
            cursor.close()
        }

        return totalDownloadedBytes
    }


    fun getTotalFilesSizeMB(downloadIds: List<Long>): Long {
        var totalSizeMB = 0L

        for (downloadId in downloadIds) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

            if (cursor.moveToFirst()) {
                if (totalBytesIndex != -1) {
                    val totalBytes = cursor.getLong(totalBytesIndex)
                    totalSizeMB += totalBytes / (1024 * 1024)
                }
            }
            cursor.close()
        }

        return totalSizeMB
    }

    fun getDownloadProgress(downloadIds: List<Long>): Int {
        var totalDownloadedBytes = 0L
        var totalTotalBytes = 0L

        for (downloadId in downloadIds) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            val downloadedBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

            if (cursor.moveToFirst()) {
                if (downloadedBytesIndex != -1 && totalBytesIndex != -1) {
                    totalDownloadedBytes += cursor.getLong(downloadedBytesIndex)
                    totalTotalBytes += cursor.getLong(totalBytesIndex)
                }
            }
            cursor.close()
        }

        return if (totalTotalBytes > 0) {
            ((totalDownloadedBytes * 100L) / totalTotalBytes).toInt()
        } else {
            0
        }
    }





}