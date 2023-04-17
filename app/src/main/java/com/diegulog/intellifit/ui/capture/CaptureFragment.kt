package com.diegulog.intellifit.ui.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diegulog.intellifit.databinding.FragmentCaptureBinding
import com.diegulog.intellifit.domain.entity.Person
import com.diegulog.intellifit.movenet.camera.CameraSourceListener
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.base.CameraFragment
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class CaptureFragment: BaseFragment<FragmentCaptureBinding>(), CameraSourceListener{
    private val start = AtomicBoolean(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cameraFragment = binding.cameraFragment.getFragment<CameraFragment>()
        cameraFragment.cameraSourceListener = this
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCaptureBinding {
        return FragmentCaptureBinding.inflate(inflater, container, false)
    }

    override fun onFPSListener(fps: Int) {
        Timber.d("fps %s", fps)
    }

    override fun onDetected(person: Person) {
        //Timber.d("person %s", person)
    }

}