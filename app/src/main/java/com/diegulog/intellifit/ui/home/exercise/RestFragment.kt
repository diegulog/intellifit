package com.diegulog.intellifit.ui.home.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.FragmentRestBinding
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RestFragment : BaseFragment<FragmentRestBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            for (i in 30 downTo 1) {
                binding.countdownTextView.text = i.toString()
                delay(1000) // wait for 1 second
            }

            goToNextExercise()
        }

        binding.next.setOnClickListener {
            goToNextExercise()
        }


    }

    fun goToNextExercise(){
        Navigation.findNavController(requireView()).navigate(RestFragmentDirections.actionRestFragmentToExerciseFragment(),
            NavOptions.Builder().setPopUpTo(R.id.RestFragment, true).build())
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRestBinding {
        return FragmentRestBinding.inflate(inflater, container, false)
    }

}