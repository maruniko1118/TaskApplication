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

        dbManager.addData(title, date, time, details, remind)
        finish()
      }
    }

    Log.d("SetFragment","onCreateView")
  }
}