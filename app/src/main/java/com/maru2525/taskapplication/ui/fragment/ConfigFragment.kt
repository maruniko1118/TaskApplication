package com.maru2525.taskapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maru2525.taskapplication.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {

  private lateinit var binding: FragmentConfigBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentConfigBinding.inflate(inflater, container, false)

    Log.d("ConfigFragment","onCreateView")
    return binding.root
  }
}