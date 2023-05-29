package com.diegulog.intellifit.ui.home.exercise

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.R
import com.diegulog.intellifit.movenet.camerax.CameraXFragment
import com.diegulog.intellifit.databinding.FragmentExerciseBinding
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.home.HomeFragment
import com.diegulog.intellifit.ui.home.training.TrainingFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class ExerciseFragment : BaseFragment<FragmentExerciseBinding>(), MenuProvider {

    private val args: ExerciseFragmentArgs by navArgs()
    private val viewModel: ExerciseViewModel by viewModel() {
        parametersOf(args.exercise)
    }

    private lateinit var cameraFragment: CameraXFragment
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this)

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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.excercise_menu, menu)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menu_preview -> {
                val direction = ExerciseFragmentDirections.actionExerciseFragmentToVideoPreviewFragment(args.exercise)
                Navigation.findNavController(requireView()).navigate(direction)

            }
            R.id.menu_capture -> {
                val direction = ExerciseFragmentDirections.actionExerciseFragmentToCaptureFragment(args.exercise)
                Navigation.findNavController(requireView()).navigate(direction)
            }
        }
        return true
    }

    override fun onDestroyView() {
        requireActivity().removeMenuProvider(this)
        super.onDestroyView()
    }

}