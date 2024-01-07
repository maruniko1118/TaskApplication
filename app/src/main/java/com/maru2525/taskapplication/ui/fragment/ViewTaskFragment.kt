package com.maru2525.taskapplication.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.database.DatabaseManager
import com.maru2525.taskapplication.databinding.FragmentViewTaskBinding
import com.maru2525.taskapplication.dialog.DetailDialogFragment
import com.maru2525.taskapplication.remind.ReceivedActivity
import com.maru2525.taskapplication.ui.PracticeRecyclerItemClickListener
import com.maru2525.taskapplication.ui.RecyclerViewTaskAdapter
import com.maru2525.taskapplication.ui.Task
import com.maru2525.taskapplication.ui.activity.RegisterActivity

class ViewTaskFragment : Fragment(), PracticeRecyclerItemClickListener.OnRecyclerClickListener {

  private lateinit var binding: FragmentViewTaskBinding

  private lateinit var recyclerView: RecyclerView

  private lateinit var adapter: RecyclerViewTaskAdapter

  // タスクテーブルのマネージャ
  private val dbTaskManager by lazy { DatabaseManager(requireActivity(), "Task") }
  // アーカイブテーブルのマネージャ
  private val dbArchiveManager by lazy { DatabaseManager(requireActivity(), "Archive") }

  private var taskData: ArrayList<Task> = arrayListOf()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentViewTaskBinding.inflate(inflater, container, false)
    recyclerView = binding.rvTask

    binding.btnAdd.setOnClickListener {
      val intent = Intent(requireActivity(), RegisterActivity::class.java)
      startActivity(intent)
    }

    Log.d("TaskFragment","onCreateView")
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    recyclerView.setHasFixedSize(true)
    val layoutManager: RecyclerView.LayoutManager
        = LinearLayoutManager(requireActivity())

    recyclerView.layoutManager = layoutManager

    taskData = dbTaskManager.getAllData()

    recyclerView.adapter = RecyclerViewTaskAdapter(taskData)

    recyclerView.addOnItemTouchListener(PracticeRecyclerItemClickListener(requireActivity(), recyclerView, this))

    val itemDecoration: RecyclerView.ItemDecoration =
      DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
    recyclerView.addItemDecoration(itemDecoration)

    adapter = recyclerView.adapter as RecyclerViewTaskAdapter

    // ドラック&ドロップの実装
    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
      // 上下のdragではonMove(), 左右のswipeではonSwiped()
      ItemTouchHelper.UP or ItemTouchHelper.DOWN,
      ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        // 開始位置と移動先の位置
        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition
        // adapterに移動したことを通知する
        adapter.notifyItemMoved(fromPos, toPos)
        return true // true if moved, false otherwise
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
          // 左スワイプ時に実行したい処理
          val data = dbTaskManager.getData(taskData[viewHolder.adapterPosition].id)
          if (data != null) {
            dbArchiveManager.addData(data.title, data.date, data.time, data.details, data.remind)
          }
          dbTaskManager.deleteData(taskData[viewHolder.adapterPosition].id)
          taskData.removeAt(viewHolder.adapterPosition)
          // adapterに削除されたことを通知する
          adapter.notifyItemRemoved(viewHolder.adapterPosition)
          // 画面下部にアーカイブ化通知
          Snackbar.make(binding.cdlTask, resources.getText(R.string.task_archive), Snackbar.LENGTH_SHORT).show()
        } else if (direction == ItemTouchHelper.RIGHT) {
          // 右スワイプ時に実行したい処理
          dbTaskManager.deleteData(taskData[viewHolder.adapterPosition].id)
          taskData.removeAt(viewHolder.adapterPosition)
          // adapterに削除されたことを通知する
          adapter.notifyItemRemoved(viewHolder.adapterPosition)
          // 画面下部に削除通知
          Snackbar.make(binding.cdlTask, resources.getText(R.string.task_delete), Snackbar.LENGTH_SHORT).show()
        }
      }

      // スワイプ時の背景色とアイコンを描画
      override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
      ) {
        super.onChildDraw(
          canvas,
          recyclerView,
          viewHolder,
          dX,
          dY,
          actionState,
          isCurrentlyActive
        )

        // 背景色を定義
        val deleteBackground = ColorDrawable(Color.RED)
        val archiveBackground = ColorDrawable(Color.GREEN)

        // スワイプ時のアイコンを定義AppCompatResources
        val deleteIcon: Drawable? =
          AppCompatResources.getDrawable(recyclerView.context, R.drawable.ic_delete)
        val archiveIcon: Drawable? =
          AppCompatResources.getDrawable(recyclerView.context, R.drawable.ic_complete)

        if (deleteIcon != null && archiveIcon != null) {

          // 余白調整
          val itemView = viewHolder.itemView
          // delete
          val deleteIconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
          val deleteIconTop =
            itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
          val deleteIconBottom = deleteIconTop + deleteIcon.intrinsicHeight
          // archive
          val archiveIconMargin = (itemView.height - archiveIcon.intrinsicHeight) / 2
          val archiveIconTop =
            itemView.top + (itemView.height - archiveIcon.intrinsicHeight) / 2
          val archiveIconBottom = deleteIconTop + archiveIcon.intrinsicHeight

          when {
            // 左方向へのスワイプ
            dX < 0 -> {
              val iconLeft =
                itemView.right - archiveIconMargin - archiveIcon.intrinsicWidth
              val iconRight = itemView.right - archiveIconMargin
              archiveIcon.setBounds(
                iconLeft,
                archiveIconTop,
                iconRight,
                archiveIconBottom
              )
              archiveBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
              )
              // 背景の描画
              archiveBackground.draw(canvas)
              // アイコンの描画
              archiveIcon.draw(canvas)
            }

            // 右方向へのスワイプ
            dX > 0 -> {
              val iconLeft = itemView.left + deleteIconMargin
              val iconRight =
                itemView.left + deleteIconMargin + deleteIcon.intrinsicWidth
              deleteIcon.setBounds(
                iconLeft,
                deleteIconTop,
                iconRight,
                deleteIconBottom
              )
              deleteBackground.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
              )
              // 背景の描画
              deleteBackground.draw(canvas)
              // アイコンの描画
              deleteIcon.draw(canvas)
            }

            else -> {
              ColorDrawable(Color.WHITE).setBounds(0, 0, 0, 0)
            }
          }
        }
      }
    })

    itemTouchHelper.attachToRecyclerView(recyclerView)

    Log.d("TaskFragment", "onViewCreated")
  }

  override fun onItemClick(view: View, position: Int) {
    DetailDialogFragment(taskData[taskData[position].id - 1]).show(parentFragmentManager, null)
    Log.d("ViewTaskFragment", "普通のタップ")
  }

  override fun onResume() {
    val tempData = dbTaskManager.getAllData()
    if (taskData.size < tempData.size) {
      dbTaskManager.getData(tempData.size)?.let { taskData.add(it) }
      adapter.notifyItemInserted(tempData.size)

      if (taskData[taskData.size-1].remind == "yes"){

        val year = taskData[taskData.size-1].date.substring(0, 4).toInt()
        val month = taskData[taskData.size-1].date.substring(5, 7).toInt()
        val day = taskData[taskData.size-1].date.substring(8, 10).toInt()
        val hour = taskData[taskData.size-1].time.substring(0, 2).toInt()
        val min = taskData[taskData.size-1].time.substring(3, 5).toInt()

        Log.d("time", "$year/$month/$day $hour:$min")

        // AlarmManagerインスタンスを取得
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // インテントを生成
        val notificationIntent = Intent(requireActivity(), ReceivedActivity::class.java)

        // Activityから値を渡したい場合は格納しておく
        notificationIntent.putExtra("message", taskData[taskData.size-1].title)
        // ブロードキャストを行うためのPendingIntentを取得
        // 第二引数requestCodeは登録するブロードキャストが1つなら0で良いが複数あるなら変更する必要有→例えばデータのIDなど
        val pendingIntent = PendingIntent.getBroadcast(requireActivity(), taskData[taskData.size-1].id, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val timeZone = TimeZone.getTimeZone("Asia/Tokyo")
        val calendar = Calendar.getInstance(timeZone)   // タイムゾーンを指定
        calendar.timeInMillis = 0                       // リセット
        calendar.set(Calendar.YEAR, year)               // 任意の年を設定
        calendar.set(Calendar.MONTH, month)             // 任意の月を設定
        calendar.set(Calendar.DAY_OF_MONTH, day)        // 任意の日を設定
        calendar.set(Calendar.HOUR_OF_DAY, hour)        // 任意の時を設定
        calendar.set(Calendar.MINUTE, min)              // 任意の分を設定
        calendar.set(Calendar.SECOND, 0)                // 任意の秒を設定
        val triggerTime = calendar.timeInMillis  // 指定した日時のミリ秒表現を取得

        Log.d("time", "$triggerTime")

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
      }
    }

    Log.d("TaskFragment", "onResume")
    super.onResume()
  }

  override fun onDestroy() {
    Log.d("TaskFragment", "onDestroy")
    dbTaskManager.closeConnect()
    dbArchiveManager.closeConnect()
    super.onDestroy()
  }
}