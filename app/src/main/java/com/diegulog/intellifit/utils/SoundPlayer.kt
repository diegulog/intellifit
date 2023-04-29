package com.diegulog.intellifit.utils

import android.content.Context
import android.media.MediaPlayer
import com.diegulog.intellifit.R

class SoundPlayer(context: Context) {

    private val beep by lazy { MediaPlayer.create(context, R.raw.beep) }
    private val start by lazy { MediaPlayer.create(context, R.raw.censor) }

    fun playBeep() {
        beep.start()
    }

    fun playStart() {
        start.start()
    }
}