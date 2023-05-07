package com.diegulog.intellifit.ui.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.diegulog.intellifit.R
import com.diegulog.intellifit.movenet.camerax.CameraXFragment
import com.diegulog.intellifit.databinding.FragmentCaptureBinding
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.movenet.camerax.CameraSourceListener
import com.diegulog.intellifit.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class CaptureFragment : BaseFragment<FragmentCaptureBinding>(), CameraSourceListener,
    CaptureViewModel.VideoCaptureListener {

    private val captureViewModel: CaptureViewModel by viewModel()
    private lateinit var cameraFragment: CameraXFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraFragment = binding.cameraFragment.getFragment()
        cameraFragment.cameraSourceListener = this
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
                captureViewModel.stopCapture {
                    findNavController().navigate(R.id.action_CaptureFragment_to_VideoPreviewFragment)
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

    override fun onFPSListener(fps: Int) {
        Timber.d("fps %s", fps)
    }

    override fun onDetected(sample: Sample) {
        captureViewModel.addPerson(sample)
    }

    override fun onStartCapture(path: String) {
        try {
            cameraFragment.startVideoRecording(
                File(path)
            )
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        }
    }

    override fun onStopCapture() {
        try {
            cameraFragment.stopVideoRecording()
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        }
    }

}