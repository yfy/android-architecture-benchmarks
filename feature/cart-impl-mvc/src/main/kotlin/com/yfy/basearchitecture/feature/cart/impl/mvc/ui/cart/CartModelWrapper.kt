package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.cart

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartModelWrapper @Inject constructor(
    val model: CartModel
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        model.onCleared()
    }
}
