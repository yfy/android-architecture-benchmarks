package com.yfy.basearchitecture.feature.product.impl.mvc.ui.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductListModelWrapper @Inject constructor(
    val model: ProductListModel
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        model.onCleared()
    }
}
