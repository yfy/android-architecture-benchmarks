package com.yfy.basearchitecture.feature.product.impl.data.repository

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.base.BaseRepository
import com.yfy.basearchitecture.core.ui.api.extensions.getJson
import com.yfy.basearchitecture.feature.product.api.ProductRepository
import com.yfy.basearchitecture.feature.product.api.model.Category
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevProductRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseRepository(), ProductRepository {

    override fun getProducts(page: Int, pageSize: Int, categoryId: String?): Flow<List<Product>> = sendRequest {
        // Dev environment'da mock data kullanıyoruz
        val allProducts = generateMockProducts()
        var filtered = allProducts
        if (categoryId != null) {
            filtered = filtered.filter { it.categoryId == categoryId }
        }
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, filtered.size)
        if (startIndex < filtered.size) filtered.subList(startIndex, endIndex) else emptyList()
    }

    override fun getProductDetail(productId: String): Flow<Product> = sendRequest {
        val allProducts = generateMockProducts()
        allProducts.find { it.id == productId }
            ?: throw Exception("Product not found")
    }

    override fun getCategories(): Flow<List<Category>> = sendRequest {
        generateMockCategories()
    }

    private fun generateMockProducts(): List<Product> {
        return (1..1000).map { index ->
            Product(
                id = "product_$index",
                name = "Product $index",
                brand = "Brand ${index % 10 + 1}",
                price = (50 + index % 1950).toDouble(),
                discountPrice = if (index % 3 == 0) (50 + index % 1950) * 0.8 else null,
                discountPercentage = if (index % 3 == 0) 20 else null,
                rating = 3.0 + (index % 20) / 10.0,
                reviewCount = (10 + index % 4990),
                imageUrl = "https://picsum.photos/seed/product$index/400/400",
                images = listOf(
                    "https://picsum.photos/seed/product$index-1/800/800",
                    "https://picsum.photos/seed/product$index-2/800/800"
                ),
                description = "High-quality product $index with advanced features and modern design.",
                features = listOf(
                    "Premium design",
                    "Quality guarantee",
                    "Advanced technology",
                    "User-friendly interface"
                ),
                stock = (0..100).random(),
                categoryId = "cat_${(index % 20) + 1}",
                sellerId = "seller_${(index % 20) + 1}",
                sellerName = "TechStore ${(index % 20) + 1}",
                freeShipping = index % 3 != 0
            )
        }
    }

    private fun generateMockCategories(): List<Category> {
        val categoryNames = listOf(
            "Electronics", "Computers", "Gaming", "Cameras", "Audio", "Wearables",
            "Home & Garden", "Automotive", "Fashion", "Sports", "Books", "Arts",
            "Kitchen", "Fitness", "Music", "TV & Video", "Tools", "Beauty",
            "Pets", "Garden"
        )
        val categoryIcons = listOf(
            "📱", "💻", "🎮", "📷", "🎧", "⌚", "🏠", "🚗", "👕", "👟",
            "📚", "🎨", "🍽️", "🏃", "🎵", "📺", "🔧", "💄", "🐕", "🌱"
        )
        
        return (1..20).map { index ->
            Category(
                id = "cat_$index",
                name = categoryNames[index - 1],
                icon = categoryIcons[index - 1],
                productCount = (50 + index * 25)
            )
        }
    }
}
