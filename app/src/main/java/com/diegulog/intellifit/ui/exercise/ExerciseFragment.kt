package com.diegulog.intellifit.ui.exercise

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.movenet.camerax.CameraXFragment
import com.diegulog.intellifit.databinding.FragmentExerciseBinding
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.home.HomeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class ExerciseFragment : BaseFragment<FragmentExerciseBinding>() {
    private val args: ExerciseFragmentArgs by navArgs()
    private val viewModel: ExerciseViewModel by viewModel() {
        parametersOf(args.exercise)
    }

    private lateinit var cameraFragment: CameraXFragment
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHostFragment<HomeFragment>()?.setTitle(args.exercise.name)

        cameraFragment = binding.cameraFragment.getFragment()
        cameraFragment.cameraSourceListener = viewModel

        viewModel.info.observe(viewLifecycleOwner) { error ->
            showMessage(error)
        }
        viewModel.outputInference.observe(viewLifecycleOwner){result ->
            binding.percent.progress = (result[0]* 100f).roundToInt()
        }
        viewModel.countReps.observe(viewLifecycleOwner){
            binding.count.text = "$it/${args.exercise.repeat}"
        }

    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExerciseBinding {
        return FragmentExerciseBinding.inflate(inflater, container, false)
    }


}