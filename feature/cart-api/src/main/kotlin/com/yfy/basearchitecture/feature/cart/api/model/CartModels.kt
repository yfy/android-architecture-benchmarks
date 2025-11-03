package com.yfy.basearchitecture.feature.cart.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val price: Double,
    val quantity: Int,
    val sellerId: String,
    val sellerName: String
)

@Serializable
data class Address(
    val id: String,
    val title: String,
    val fullName: String,
    val phoneNumber: String,
    val city: String,
    val district: String,
    val addressLine: String,
    val isDefault: Boolean = false
)

@Serializable
data class Order(
    val id: String,
    val items: List<CartItem>,
    val address: Address,
    val subtotal: Double,
    val shippingCost: Double,
    val total: Double,
    val status: OrderStatus,
    val createdAt: Long
)

@Serializable
enum class OrderStatus { 
    PENDING, 
    CONFIRMED, 
    SHIPPED, 
    DELIVERED, 
    CANCELLED 
}

@Serializable
enum class CheckoutStep { 
    ADDRESS, 
    PAYMENT, 
    CONFIRMATION 
}
