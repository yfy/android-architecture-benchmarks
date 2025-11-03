package com.yfy.basearchitecture.feature.chat.impl.mvp.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatDetailPresenterWrapper @Inject constructor(
    val presenter: ChatDetailPresenter
) : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
}
