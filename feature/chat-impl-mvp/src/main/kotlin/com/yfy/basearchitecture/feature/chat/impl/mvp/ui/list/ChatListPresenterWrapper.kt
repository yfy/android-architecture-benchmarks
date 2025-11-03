package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListPresenterWrapper @Inject constructor(
    val presenter: ChatListPresenter
) : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
}
