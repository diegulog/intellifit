package com.diegulog.intellifit.ui.home.exercise

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.DialogDescriptionBinding
import com.diegulog.intellifit.movenet.camerax.CameraXFragment
import com.diegulog.intellifit.databinding.FragmentExerciseBinding
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.home.HomeFragment
import com.diegulog.intellifit.ui.home.details.DetailsViewModel
import com.diegulog.intellifit.utils.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

class ExerciseFragment : BaseFragment<FragmentExerciseBinding>(), MenuProvider {

    private val detailsViewModel: DetailsViewModel by activityViewModels()
    private val viewModel: ExerciseViewModel by viewModel() {
        parametersOf(detailsViewModel.getCurrentExercise())
    }

    private lateinit var cameraFragment: CameraXFragment

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this)

        getHostFragment<HomeFragment>()?.setTitle(detailsViewModel.getCurrentExercise().name)

        cameraFragment = binding.cameraFragment.getFragment()
        cameraFragment.cameraSourceListener = viewModel

        viewModel.info.observe(viewLifecycleOwner) { error ->
            showMessage(error)
        }
        viewModel.outputInference.observe(viewLifecycleOwner) { result ->
            binding.percent.progress = (result[0] * 100f).roundToInt()
        }
        viewModel.countReps.observe(viewLifecycleOwner) {
            binding.count.text = "$it/${detailsViewModel.getCurrentExercise().repeat}"
            if (it >= detailsViewModel.getCurrentExercise().repeat) {
                if (detailsViewModel.start.value == true) {
                    viewModel.playBeep()
                    if (detailsViewModel.getNextExercise() != null) {
                        Navigation.findNavController(view)
                            .navigate(
                                ExerciseFragmentDirections.actionExerciseFragmentToRestFragment(),
                                NavOptions.Builder().setPopUpTo(R.id.ExerciseFragment, true).build()
                            )
                    } else {
                        Navigation.findNavController(view).navigateUp()
                    }

                }

            }
        }

        if (!detailsViewModel.getCurrentExercise().showDescription) {
            detailsViewModel.getCurrentExercise().showDescription = true
            val dialog = getDialogDescription()
            dialog.show()
            lifecycleScope.launch {
                delay(5000)  // delay for 5 seconds
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }


    }

    private fun getDialogDescription(): Dialog {
        val dialogBinding = DialogDescriptionBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding.description.text = detailsViewModel.getCurrentExercise().description
        dialogBinding.image.load(detailsViewModel.getCurrentExercise().urlImage)

        return MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok, null)
            .create()
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
        when (menuItem.itemId) {
            R.id.menu_preview -> {
                val direction =
                    ExerciseFragmentDirections.actionExerciseFragmentToVideoPreviewFragment()
                Navigation.findNavController(requireView()).navigate(direction)

            }

            R.id.menu_capture -> {
                val direction =
                    ExerciseFragmentDirections.actionExerciseFragmentToCaptureFragment()
                Navigation.findNavController(requireView()).navigate(direction)
            }

            R.id.menu_description -> {
                Navigation.findNavController(requireView())
                    .navigate(
                        ExerciseFragmentDirections.actionExerciseFragmentToRestFragment(),
                        NavOptions.Builder().setPopUpTo(R.id.ExerciseFragment, true).build()
                    )
            }
        }
        return true
    }

    override fun onDestroyView() {
        requireActivity().removeMenuProvider(this)
        super.onDestroyView()
    }

}