package com.diegulog.intellifit.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {
    protected val _info = Channel<String>(Channel.BUFFERED)
    val info = _info.receiveAsFlow().asLiveData()
}