package com.jasmeet.myapplication.broadCastReceiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadCompletedReceiver :BroadcastReceiver(){

    private lateinit var downloadManger :DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {

        downloadManger = context?.getSystemService(DownloadManager::class.java)!!

        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE"){
            val id = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, -1
            )
            val query = DownloadManager.Query()
            if (id != -1L){
                println("Download with ID $id finished!")
            }
        }
    }
}