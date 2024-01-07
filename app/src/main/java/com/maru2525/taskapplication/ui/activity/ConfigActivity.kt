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
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.remind.ReceivedActivity

class ConfigActivity : AppCompatActivity() {

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_config)

    // 通知を発行ボタン
    val buttonNotification: Button = findViewById(R.id.btnRemindTest)
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
      val triggerTime = SystemClock.elapsedRealtime() + 3000L // 3秒後
      // アラームの設定
      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)

      Log.d("time", (SystemClock.elapsedRealtime() + 3000L).toString())
    }
  }
}