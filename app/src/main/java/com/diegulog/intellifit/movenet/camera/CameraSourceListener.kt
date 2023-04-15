package com.diegulog.intellifit.movenet.camera

import com.diegulog.intellifit.domain.entity.Person

interface CameraSourceListener {
    fun onFPSListener(fps: Int)
    fun onDetected(person: Person)

}