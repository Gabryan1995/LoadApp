package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.database.Cursor

import android.R.attr.name
import android.app.DownloadManager.Query


private val NOTIFICATION_ID = 0
const val EXTRA_FILENAME = "com.udacity.LoadApp.FILENAME"
const val EXTRA_STATUS = "com.udacity.LoadApp.STATUS"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var downloadUrl: String
    private lateinit var fileName: String
    private lateinit var status: String
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor = downloadManager.query(Query().setFilterById(id!!))

            if (cursor.moveToNext()) {
                val currStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                cursor.close()
                if (currStatus == DownloadManager.STATUS_FAILED) {
                    status = "Failed"
                }
                else if (currStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    status = "Success"
                }
            }

            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                applicationContext,
                fileName,
                status
            )
        }
    }

    private fun download() {
        val selectedRadioButton = radio_group.checkedRadioButtonId

        if (selectedRadioButton == -1) {
            Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
        } else {
            val request =
                DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            when (view.getId()) {
                R.id.glide_button -> {
                    downloadUrl = "https://github.com/bumptech/glide.git"
                    fileName = applicationContext.getString(R.string.glide_button_text)
                }
                R.id.loadapp_button -> {
                    downloadUrl = URL
                    fileName = applicationContext.getString(R.string.loadapp_button_text)
                }
                R.id.retrofit_button -> {
                    downloadUrl = "https://github.com/square/retrofit.git"
                    fileName = applicationContext.getString(R.string.retrofit_button_text)
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableVibration(true)
            notificationChannel.description = "Repository downloaded"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}

// Extension Function for NotificationManager
fun NotificationManager.sendNotification(applicationContext: Context, fileName: String, status: String) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(EXTRA_FILENAME, fileName)
        putExtra(EXTRA_STATUS, status)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getText(R.string.notification_description).toString())
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}
