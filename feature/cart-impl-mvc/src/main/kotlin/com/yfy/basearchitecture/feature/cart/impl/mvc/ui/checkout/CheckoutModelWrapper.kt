package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.checkout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutModelWrapper @Inject constructor(
    val model: CheckoutModel
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        model.onCleared()
    }
}
