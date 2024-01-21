package com.maru2525.taskapplication.ui.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.databinding.ActivityConfigBinding
import com.maru2525.taskapplication.databinding.ActivityRegisterBinding
import com.maru2525.taskapplication.remind.ReceivedActivity
import com.maru2525.taskapplication.remind.ReceivedAlarmActivity

class ConfigActivity : AppCompatActivity() {

  private lateinit var binding: ActivityConfigBinding

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityConfigBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // 通知を発行ボタン
    val buttonNotification: Button = binding.btnRemindTest
    buttonNotification.setOnClickListener {
      // AlarmManagerインスタンスを取得
      val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
      // インテントを生成
      val notificationIntent = Intent(this, ReceivedActivity::class.java)
      // Activityから値を渡したい場合は格納しておく
      notificationIntent.putExtra("メッセージ", "渡したいメッセージ")
      // ブロードキャストを行うためのPendingIntentを取得
      // 第二引数requestCodeは登録するブロードキャストが1つなら0で良いが複数あるなら変更する必要有→例えばデータのIDなど
      val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

      // 実行される時間を定義
      // SystemClock.elapsedRealtime ： システム起動後の経過時間をミリ秒単位で取得するメソッド
      val triggerTime = SystemClock.elapsedRealtime() // 3秒後
      // アラームの設定
      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)

    }
    // アラームを発行ボタン
    val buttonAlarm: Button = binding.btnAlarmTest
    buttonAlarm.setOnClickListener {
      // AlarmManagerインスタンスを取得
      val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
      // インテントを生成
      val notificationIntent = Intent(this, ReceivedAlarmActivity::class.java)
      // Activityから値を渡したい場合は格納しておく
      notificationIntent.putExtra("メッセージ", "渡したいメッセージ")
      // ブロードキャストを行うためのPendingIntentを取得
      // 第二引数requestCodeは登録するブロードキャストが1つなら0で良いが複数あるなら変更する必要有→例えばデータのIDなど
      val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

      // 実行される時間を定義
      // SystemClock.elapsedRealtime ： システム起動後の経過時間をミリ秒単位で取得するメソッド
      val triggerTime = SystemClock.elapsedRealtime() // 3秒後
      // アラームの設定
      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)

    }
  }
}