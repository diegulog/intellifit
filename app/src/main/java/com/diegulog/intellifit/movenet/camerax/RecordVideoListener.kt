package com.diegulog.intellifit.movenet.camerax

import android.net.Uri

interface RecordVideoListener {
    fun onVideoSaved(path: Uri)
    fun onError(message: String, cause: Throwable?)
}