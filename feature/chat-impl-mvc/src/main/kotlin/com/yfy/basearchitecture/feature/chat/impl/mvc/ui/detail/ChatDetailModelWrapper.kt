package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatDetailModelWrapper @Inject constructor(
    val model: ChatDetailModel
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        model.onCleared()
    }
}
