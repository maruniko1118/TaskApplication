package com.maru2525.taskapplication.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.database.DatabaseManager
import com.maru2525.taskapplication.databinding.ActivityRegisterBinding
import com.maru2525.taskapplication.dialog.DatePickerDialogFragment
import com.maru2525.taskapplication.dialog.TimePickerDialogFragment

class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding

  private val dbManager by lazy { DatabaseManager(this, "Task") }

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

    binding.btnSave.setOnClickListener {
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
      dbManager.addData(title, date, time, details)
      finish()
    }

    Log.d("SetFragment","onCreateView")
  }

}