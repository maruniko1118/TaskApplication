package com.maru2525.taskapplication.ui

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

class PracticeRecyclerItemClickListener(context: Context, recyclerView: RecyclerView, private val listener: OnRecyclerClickListener)
  : RecyclerView.SimpleOnItemTouchListener() {

  interface OnRecyclerClickListener{
    fun onItemClick(view: View, position: Int)
  }

  private val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {

      val childView = recyclerView.findChildViewUnder(e.x, e.y)

      if (childView != null) {
        listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView))
      }
      return true
    }
  })

  override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
    val result = gestureDetector.onTouchEvent(e)

    return result
  }
}