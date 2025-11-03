package com.yfy.basearchitecture.feature.product.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val price: Double,
    val discountPrice: Double? = null,
    val discountPercentage: Int? = null,
    val rating: Double,
    val reviewCount: Int,
    val imageUrl: String,
    val images: List<String>,
    val description: String,
    val features: List<String>,
    val stock: Int,
    val categoryId: String,
    val sellerId: String,
    val sellerName: String,
    val freeShipping: Boolean = false
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val productCount: Int
)
