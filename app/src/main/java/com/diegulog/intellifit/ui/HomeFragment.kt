package com.diegulog.intellifit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.FragmentHomeBinding
import com.diegulog.intellifit.ui.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_CaptureFragment)
        }
    }

}