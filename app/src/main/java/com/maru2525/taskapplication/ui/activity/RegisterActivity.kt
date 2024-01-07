package com.maru2525.taskapplication.ui.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.database.DatabaseManager
import com.maru2525.taskapplication.databinding.ActivityRegisterBinding
import com.maru2525.taskapplication.dialog.DatePickerDialogFragment
import com.maru2525.taskapplication.dialog.TimePickerDialogFragment
import com.maru2525.taskapplication.remind.CHANNEL_ID
import com.maru2525.taskapplication.remind.ReceivedActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding

  private val dbManager by lazy { DatabaseManager(this, "Task") }

  private var remind = "no"
  private var year = 0
  private var month = 0
  private var day = 0
  private var hour = 0
  private var min = 0

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityRegisterBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnDate.setOnClickListener {
      DatePickerDialogFragment(binding.tvDate).show(
        supportFragmentManager,
        null)
    }

    binding.btnDateDelete.setOnClickListener {
      binding.tvDate.text = resources.getText(R.string.set_date)
    }

    binding.btnTime.setOnClickListener {
      TimePickerDialogFragment(binding.tvTime).show(
        supportFragmentManager,
        null)
    }

    binding.btnTimeDelete.setOnClickListener {
      binding.tvTime.text = resources.getText(R.string.set_time)
    }

    binding.swUseRemind.setOnCheckedChangeListener { _, isChecked ->
      remind = if (isChecked) {
        "yes"
      } else {
        "no"
      }
    }

    binding.btnSave.setOnClickListener {
      if (remind == "yes" && (binding.tvDate.text.toString() == resources.getString(R.string.set_date) || binding.tvTime.text.toString() == resources.getString(R.string.set_time))){
        Toast.makeText(this, "未入力の項目があります",  Toast.LENGTH_SHORT).show()
      }
      else {
        var title = binding.edtTitle.text.toString()
        if (title == "")
          title = resources.getString(R.string.no_title)
        var date = binding.tvDate.text.toString()
        if (date == resources.getString(R.string.set_date))
          date = ""
        var time = binding.tvTime.text.toString()
        if (time == resources.getString(R.string.set_time))
          time = ""
        var details = binding.edtDetail.text.toString()
        if (details == "")
          details = ""

        if (remind == "yes") {
          year = date.substring(0, 4).toInt()
          month = date.substring(5, 7).toInt()
          day = date.substring(8, 10).toInt()
          hour = time.substring(0, 2).toInt()
          min = time.substring(3, 5).toInt()

          Log.d("time", "$year/$month/$day $hour:$min")

          // AlarmManagerインスタンスを取得
          val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
          // インテントを生成
          val notificationIntent = Intent(this, ReceivedActivity::class.java)

          // Activityから値を渡したい場合は格納しておく
          notificationIntent.putExtra("message", title)
          // ブロードキャストを行うためのPendingIntentを取得
          // 第二引数requestCodeは登録するブロードキャストが1つなら0で良いが複数あるなら変更する必要有→例えばデータのIDなど
          val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

          val timeZone = TimeZone.getTimeZone("Asia/Tokyo")
          val calendar = Calendar.getInstance(timeZone)   // タイムゾーンを指定
          calendar.timeInMillis = 0                       // リセット
          calendar.set(Calendar.YEAR, year)               // 任意の年を設定
          calendar.set(Calendar.MONTH, month)             // 任意の月を設定
          calendar.set(Calendar.DAY_OF_MONTH, day)        // 任意の日を設定
          calendar.set(Calendar.HOUR_OF_DAY, hour)        // 任意の時を設定
          calendar.set(Calendar.MINUTE, min)              // 任意の分を設定
          calendar.set(Calendar.SECOND, 0)                // 任意の秒を設定
          val triggerTime = calendar.timeInMillis.toString().substring(0, calendar.timeInMillis.toString().length - 4).toLong()    // 指定した日時のミリ秒表現を取得

          Log.d("time", "$triggerTime")

          alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }

        dbManager.addData(title, date, time, details, remind)
        finish()
      }
    }

    Log.d("SetFragment","onCreateView")
  }
}