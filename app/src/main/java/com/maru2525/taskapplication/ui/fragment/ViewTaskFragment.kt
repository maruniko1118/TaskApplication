package com.maru2525.taskapplication.ui.fragment

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.maru2525.taskapplication.R
import com.maru2525.taskapplication.database.DatabaseArchiveManager
import com.maru2525.taskapplication.database.DatabaseTaskManager
import com.maru2525.taskapplication.databinding.FragmentViewTaskBinding
import com.maru2525.taskapplication.dialog.DetailDialogFragment
import com.maru2525.taskapplication.ui.PracticeRecyclerItemClickListener
import com.maru2525.taskapplication.ui.RecyclerViewTaskAdapter
import com.maru2525.taskapplication.ui.Task
import com.maru2525.taskapplication.ui.activity.RegisterActivity

class ViewTaskFragment : Fragment(), PracticeRecyclerItemClickListener.OnRecyclerClickListener {

  private lateinit var binding: FragmentViewTaskBinding

  private lateinit var recyclerView: RecyclerView

  private lateinit var adapter: RecyclerViewTaskAdapter

  // タスクテーブルのマネージャ
  private val dbTaskManager by lazy { DatabaseTaskManager(requireActivity()) }
  // アーカイブテーブルのマネージャ
  private val dbArchiveManager by lazy { DatabaseArchiveManager(requireActivity()) }

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
          dbTaskManager.deleteData(taskData[viewHolder.adapterPosition].id)
          taskData.removeAt(viewHolder.adapterPosition)
          // adapterに削除されたことを通知する
          adapter.notifyItemRemoved(viewHolder.adapterPosition)
          // 画面下部に削除通知
          Snackbar.make(binding.cdlTask, resources.getText(R.string.task_delete), Snackbar.LENGTH_SHORT).show()
        } else if (direction == ItemTouchHelper.RIGHT) {
          // 右スワイプ時に実行したい処理
          val data = dbTaskManager.getData(taskData[viewHolder.adapterPosition].id)
          if (data != null) {
            dbArchiveManager.addData(data.title, data.date, data.time)
          }
          dbTaskManager.deleteData(taskData[viewHolder.adapterPosition].id)
          taskData.removeAt(viewHolder.adapterPosition)
          // adapterに削除されたことを通知する
          adapter.notifyItemRemoved(viewHolder.adapterPosition)
          // 画面下部にアーカイブ化通知
          Snackbar.make(binding.cdlTask, resources.getText(R.string.task_archive), Snackbar.LENGTH_SHORT).show()
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
            dX < 0 -> { // 左方向へのスワイプ
              val iconLeft =
                itemView.right - deleteIconMargin - deleteIcon.intrinsicWidth
              val iconRight = itemView.right - deleteIconMargin
              deleteIcon.setBounds(
                iconLeft,
                deleteIconTop,
                iconRight,
                deleteIconBottom
              )
              deleteBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
              )
              // 背景の描画
              deleteBackground.draw(canvas)
              // アイコンの描画
              deleteIcon.draw(canvas)
            }

            dX > 0 -> { // 右方向へのスワイプ
              val iconLeft = itemView.left + archiveIconMargin
              val iconRight =
                itemView.left + archiveIconMargin + archiveIcon.intrinsicWidth
              archiveIcon.setBounds(
                iconLeft,
                archiveIconTop,
                iconRight,
                archiveIconBottom
              )
              archiveBackground.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
              )
              // 背景の描画
              archiveBackground.draw(canvas)
              // アイコンの描画
              archiveIcon.draw(canvas)
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