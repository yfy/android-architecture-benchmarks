package com.yfy.basearchitecture.feature.product.impl.mvi.ui.list

sealed interface ProductListIntent {
    object LoadProducts : ProductListIntent
    object LoadMore : ProductListIntent
    object Refresh : ProductListIntent
    data class CategorySelected(val categoryId: String) : ProductListIntent
    data class ProductClicked(val productId: String) : ProductListIntent
    object CartClicked : ProductListIntent
    object ChatClicked : ProductListIntent
}
