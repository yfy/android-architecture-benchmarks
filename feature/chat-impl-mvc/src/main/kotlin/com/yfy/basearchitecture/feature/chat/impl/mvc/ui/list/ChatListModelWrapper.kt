package com.yfy.basearchitecture.feature.chat.impl.mvc.ui.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListModelWrapper @Inject constructor(
    val model: ChatListModel
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        model.onCleared()
    }
}
