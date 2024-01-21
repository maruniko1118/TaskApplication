package com.maru2525.taskapplication.remind

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.maru2525.taskapplication.ui.activity.AlarmActivity
import com.maru2525.taskapplication.ui.activity.MainActivity


const val CHANNEL_ID_ALARM = "アラームの時間" // 共通のチャンネルID

class ReceivedAlarmActivity: BroadcastReceiver() {

  @SuppressLint("MissingPermission")
  override fun onReceive(context: Context, intent: Intent) {

    val intentAlarm = Intent(context, MainActivity::class.java)
    context.startActivity(intentAlarm)

    val notification = Intent(context, AlarmActivity::class.java)
    //起動するアクティビティを取得
    //起動するアクティビティを取得
    notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(notification)
    //アクティビティを起動
  }
}