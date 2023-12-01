package com.maru2525.taskapplication.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TimePickerDialogFragment(private val time: TextView): DialogFragment(), TimePickerDialog.OnTimeSetListener{

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    //インスタンス
    val c = Calendar.getInstance()
    //最初に、システムの現在の時間と分を取得
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)

    return TimePickerDialog(
      requireContext(),
      this,//返したいアクティビティをセット
      hour,
      minute,
      true)
  }

  //時間選択がされた時の処理
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
    //TimePickerにて選択された時間(ジフン)をもとにLocalDateのインスタンスを取得
    val setLocalTime = LocalTime.of(hourOfDay, minute)
    //表示する際のフォーマットを決める
    val format = DateTimeFormatter.ofPattern("hh:mm")
    //選択された日付をフォーマットに合わせる
    val time = setLocalTime.format(format)
    //editTextに表示させる
    this.time.text = time
  }
}