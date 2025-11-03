package com.yfy.basearchitecture.feature.product.impl.mvp.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailPresenterWrapper @Inject constructor(
    val presenter: ProductDetailPresenter
) : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
}
