package com.yfy.basearchitecture.feature.cart.impl.classicmvvm.data.local

import com.yfy.basearchitecture.core.datastore.api.interfaces.PreferenceManager
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.product.api.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartSessionManager @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var isInitialized = false

    init {
        scope.launch {
            loadCartFromPreferences()
            isInitialized = true
        }
    }
    
    fun isReady(): Boolean = isInitialized

    private suspend fun loadCartFromPreferences() {
        try {
            val items = preferenceManager.getObject(
                key = CART_KEY,
                serializer = serializer<List<CartItem>>(),
                defaultValue = emptyList()
            ).first()
            _cartItems.value = items
        } catch (e: Exception) {
            _cartItems.value = emptyList()
        }
    }

    suspend fun addToCart(product: Product, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.productId == product.id }
        
        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + quantity
            )
        } else {
            currentItems.add(product.toCartItem(quantity))
        }
        
        _cartItems.value = currentItems
        saveCartToPreferences(currentItems)
    }

    suspend fun updateCartItem(cartItemId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.id == cartItemId }
        
        if (itemIndex != -1) {
            if (quantity <= 0) {
                currentItems.removeAt(itemIndex)
            } else {
                val item = currentItems[itemIndex]
                currentItems[itemIndex] = item.copy(
                    quantity = quantity
                )
            }
            _cartItems.value = currentItems
            saveCartToPreferences(currentItems)
        }
    }

    suspend fun removeFromCart(cartItemId: String) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.id == cartItemId }
        _cartItems.value = currentItems
        saveCartToPreferences(currentItems)
    }

    suspend fun clearCart() {
        _cartItems.value = emptyList()
        preferenceManager.remove(CART_KEY)
    }

    suspend fun resetCartOnAppStart() {
        clearCart()
    }

    private suspend fun saveCartToPreferences(cartItems: List<CartItem>) {
        try {
            preferenceManager.setObject(
                key = CART_KEY,
                value = cartItems,
                serializer = serializer<List<CartItem>>()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _cartItems.value = emptyList()
        }
    }

    private fun Product.toCartItem(quantity: Int): CartItem =
        CartItem(
            id = "cart_item_${System.currentTimeMillis()}",
            productId = this.id,
            productName = this.name,
            productImage = this.imageUrl,
            price = this.price,
            quantity = quantity,
            sellerId = this.sellerId,
            sellerName = this.sellerName
        )

    companion object {
        private const val CART_KEY = "cart_items"
    }
}