package com.maru2525.taskapplication.dialog

import android.os.Bundle
import android.text.Editable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.maru2525.taskapplication.databinding.FragmentDetailDialogBinding
import com.maru2525.taskapplication.ui.Task

class DetailDialogFragment(private val task: Task) : DialogFragment(){

  private lateinit var binding : FragmentDetailDialogBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentDetailDialogBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.title.text = Editable.Factory.getInstance().newEditable(task.title)

    binding.date.text = task.date
    binding.time.text = task.time
  }

  override fun onStart() {
    super.onStart()

    val metrics : DisplayMetrics = requireActivity().resources.displayMetrics

    val width = (resources.displayMetrics.widthPixels - 1024 / metrics.density).toInt()
    val height = (resources.displayMetrics.heightPixels - 2048 / metrics.density).toInt()
    dialog?.window?.setLayout(width, height)
  }
}
