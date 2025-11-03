package com.yfy.basearchitecture.feature.cart.impl.mvp.ui.checkout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutPresenterWrapper @Inject constructor(
    val presenter: CheckoutPresenter
) : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
}
