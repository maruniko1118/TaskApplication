package com.maru2525.taskapplication.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class DatePickerDialogFragment(private val date: TextView): DialogFragment(), DatePickerDialog.OnDateSetListener{

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

    //Calender()を取得
    val calendar: Calendar = Calendar.getInstance()
    //年を示すフィールドの値を取得
    val year = calendar.get(Calendar.YEAR)
    //月を示すフィールドの値を取得
    val month = calendar.get(Calendar.MONTH)
    //日を示すフィールドの値を取得
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    //指定された日付でDatePickerを作成して返す
    return DatePickerDialog(
      requireContext(),
      this,
      year,
      month,
      day
    )
  }

  //日付選択がされた時の処理
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    //DatePickerにて選択された日付(年月日)をもとにLocalDateのインスタンスを取得
    val setLocalDate = LocalDate.of(year,month+1,dayOfMonth)
    //表示する際のフォーマットを決める
    val format = DateTimeFormatter.ofPattern("yyyy/MM/dd(E)")
    //選択された日付をフォーマットに合わせる
    val date = setLocalDate.format(format)
    //editTextに表示させる
    this.date.text = date
  }
}