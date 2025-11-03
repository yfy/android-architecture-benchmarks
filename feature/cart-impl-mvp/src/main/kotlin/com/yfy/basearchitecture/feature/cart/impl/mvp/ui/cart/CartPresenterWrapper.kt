package com.yfy.basearchitecture.feature.cart.impl.mvp.ui.cart

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartPresenterWrapper @Inject constructor(
    val presenter: CartPresenter
) : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
}
