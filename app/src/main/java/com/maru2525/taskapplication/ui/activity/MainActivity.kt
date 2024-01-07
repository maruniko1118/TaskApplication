package com.maru2525.taskapplication.ui.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.databinding.ActivityMainBinding
import com.maru2525.taskapplication.remind.CHANNEL_ID


class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // 許可ダイアログを表示
    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)

    // チャンネルの生成
    createNotificationChannel()

    val navView: BottomNavigationView = binding.navView

    val navController = findNavController(R.id.nav_host_fragment_activity_main)
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    val appBarConfiguration = AppBarConfiguration(
      setOf(
        R.id.navigation_task, R.id.navigation_archive
      )
    )
    setupActionBarWithNavController(navController, appBarConfiguration)
    navView.setupWithNavController(navController)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_button -> {
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  // 参考 --> https://tech.amefure.com/android-notify
  private val launcher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
      // ダイアログの結果で処理を分岐
      if (result) {
        Log.d("POST_NOTIFICATIONS", "許可")
      } else {
        Log.e("POST_NOTIFICATIONS", "非許可")
      }
    }

  //  通知チャンネルの作成
  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "チャンネル名"
      val descriptionText = "チャンネルの説明"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
      }
      // チャンネルをシステムに登録
      val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
}