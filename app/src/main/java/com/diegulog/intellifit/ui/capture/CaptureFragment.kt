package com.diegulog.intellifit.ui.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.FragmentCaptureBinding
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.movenet.camera.CameraSourceListener
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.base.CameraFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CaptureFragment : BaseFragment<FragmentCaptureBinding>(), CameraSourceListener {

    val captureViewModel: CaptureViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cameraFragment = binding.cameraFragment.getFragment<CameraFragment>()
        cameraFragment.cameraSourceListener = this
        captureViewModel.isCapture.observe(viewLifecycleOwner) {
            binding.btnPlay.setText(if(it) R.string.stop_capture else R.string.start_capture)
            binding.btnPlay.icon = ContextCompat.getDrawable(requireContext(),
                if(it)
                    R.drawable.ic_stop_circle
                else
                    R.drawable.ic_play_circle)
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
                captureViewModel.startCapture()
            } else {
                captureViewModel.stopCapture()
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

}