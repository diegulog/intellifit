package com.diegulog.intellifit.ui.home.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.R
import com.diegulog.intellifit.movenet.camerax.CameraXFragment
import com.diegulog.intellifit.databinding.FragmentCaptureBinding
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.home.HomeFragment
import com.diegulog.intellifit.ui.home.details.DetailsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.io.File

class CaptureFragment : BaseFragment<FragmentCaptureBinding>(),
    CaptureViewModel.VideoCaptureListener {

    private val detailsViewModel: DetailsViewModel by activityViewModels()

    private val captureViewModel: CaptureViewModel by viewModel {
        parametersOf(detailsViewModel.getCurrentExercise())
    }


    private lateinit var cameraFragment: CameraXFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHostFragment<HomeFragment>()?.setTitle(getString(R.string.training) + " - " + detailsViewModel.getCurrentExercise().name)

        cameraFragment = binding.cameraFragment.getFragment()
        cameraFragment.cameraSourceListener = captureViewModel
        captureViewModel.isCapture.observe(viewLifecycleOwner) {
            binding.btnPlay.setText(if (it) R.string.stop_capture else R.string.start_capture)
            binding.btnPlay.icon = ContextCompat.getDrawable(
                requireContext(),
                if (it)
                    R.drawable.ic_stop_circle
                else
                    R.drawable.ic_play_circle
            )
        }

        captureViewModel.message.observe(viewLifecycleOwner) { message ->
            binding.time.text = message
            when (message) {
                "START" -> changeColorTime(R.color.green)
                "STOP" -> changeColorTime(R.color.red)
            }
        }
        captureViewModel.info.observe(viewLifecycleOwner) { error ->
            showMessage(error)
        }


        binding.btnPlay.setOnClickListener {
            if (captureViewModel.isCapture.value == false) {
                captureViewModel.startCapture(videoCaptureListener = this)
            } else {
                binding.btnPlay.isEnabled = false
                lifecycleScope.launch {
                    captureViewModel.stopCapture()
                    binding.btnPlay.isEnabled = true
                    val direction = CaptureFragmentDirections.actionCaptureFragmentToVideoPreviewFragment()
                    Navigation.findNavController(requireView()).navigate(direction)
                }
            }
        }
    }

    private fun changeColorTime(@ColorRes id: Int) {
        binding.time.setTextColor(ContextCompat.getColor(requireContext(), id))
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCaptureBinding {
        return FragmentCaptureBinding.inflate(inflater, container, false)
    }

    override fun onStartCapture(path: String) {
        try {
            cameraFragment.startVideoRecording(
                File(path)
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
    override fun onStopCapture() {
        try {
            cameraFragment.stopVideoRecording()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

}