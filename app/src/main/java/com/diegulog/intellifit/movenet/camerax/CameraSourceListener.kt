package com.diegulog.intellifit.movenet.camerax

import com.diegulog.intellifit.domain.entity.Sample

interface CameraSourceListener {
    fun onFPSListener(fps: Int)
    fun onDetected(sample: Sample)

}