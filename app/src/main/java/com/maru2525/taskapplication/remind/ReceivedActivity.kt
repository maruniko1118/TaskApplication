package com.maru2525.taskapplication.remind

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maru2525.taskapplication.R

const val CHANNEL_ID_NOTIFICATION = "タスクの時間" // 共通のチャンネルID

class ReceivedActivity : BroadcastReceiver() {

  private val NOTIFY_ID = 1

  @SuppressLint("MissingPermission")
  override fun onReceive(context: Context, intent: Intent) {

    // ブロードキャストを受け取る
    val receivedData = intent.getStringExtra("message")

    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_IMMUTABLE)

    // 通知オブジェクトの作成
    var builder = NotificationCompat.Builder(context, CHANNEL_ID_NOTIFICATION)
      .setSmallIcon(R.drawable.ic_top)
      .setContentTitle("タスクの時間")
      .setContentText(receivedData)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    // 通知の発行
    with(NotificationManagerCompat.from(context)) {
      notify(NOTIFY_ID, builder.build())
    }
  }
}