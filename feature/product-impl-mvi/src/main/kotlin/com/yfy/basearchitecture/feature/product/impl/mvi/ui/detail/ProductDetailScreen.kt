package com.yfy.basearchitecture.feature.product.impl.mvi.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yfy.basearchitecture.core.ui.api.base.BaseScreenScaffold
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.mvi.R

@Composable
fun ProductDetailScreen(
    productId: String, 
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) { 
        viewModel.handleIntent(ProductDetailIntent.LoadProduct(productId))
    }
    BaseScreenScaffold(
        viewModel = viewModel, 
        screenName = "ProductDetailScreen"
    ) {
        val state = viewModel.state.collectAsState().value
        state.product?.let { product ->
            ProductDetailContent(
                product = product, 
                quantity = state.quantity, 
                onQuantityChange = { viewModel.handleIntent(ProductDetailIntent.QuantityChanged(it)) }, 
                onAddToCart = { viewModel.handleIntent(ProductDetailIntent.AddToCart) }, 
                onBackClick = { viewModel.handleIntent(ProductDetailIntent.NavigateBack) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailContent(
    product: Product, 
    quantity: Int, 
    onQuantityChange: (Int) -> Unit, 
    onAddToCart: () -> Unit,
    onBackClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { product.images.size })
    Scaffold(
        topBar = { 
            TopAppBar(
                title = {}, 
                navigationIcon = { 
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") 
                    } 
                }
            ) 
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { onQuantityChange((quantity - 1).coerceAtLeast(1)) }) { 
                            Icon(Icons.Default.Remove, contentDescription = "Decrease") 
                        }
                        Text(text = quantity.toString(), style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { onQuantityChange((quantity + 1).coerceAtMost(product.stock)) }) { 
                            Icon(Icons.Default.Add, contentDescription = "Increase") 
                        }
                    }
                    Button(onClick = onAddToCart, modifier = Modifier.weight(1f)) {
                        Text(context.getString(R.string.feature_product_impl_add_to_cart))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(paddingValues)
        ) {
            item {
                Box {
                    HorizontalPager(
                        state = pagerState, 
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    ) { page ->
                        AsyncImage(
                            model = product.images.getOrNull(page) ?: product.imageUrl, 
                            contentDescription = null, 
                            modifier = Modifier.fillMaxSize(), 
                            contentScale = ContentScale.Crop
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), 
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(product.images.size) { index ->
                            Box(
                                modifier = Modifier.size(8.dp).clip(CircleShape).background(
                                    if (index == pagerState.currentPage) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = product.name, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.brand, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star, 
                            contentDescription = null, 
                            tint = Color(0xFFFFA000), 
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.rating} (${product.reviewCount} ${context.getString(R.string.feature_product_impl_reviews)})", 
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${product.discountPrice ?: product.price} TL",
                            style = MaterialTheme.typography.headlineMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (product.discountPrice != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${product.price} TL", 
                                style = MaterialTheme.typography.bodyLarge, 
                                textDecoration = TextDecoration.LineThrough, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer, 
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "%${product.discountPercentage} ↓", 
                                    style = MaterialTheme.typography.labelMedium, 
                                    color = MaterialTheme.colorScheme.onErrorContainer, 
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    if (product.freeShipping) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🚚", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = context.getString(R.string.feature_product_impl_free_shipping), 
                                style = MaterialTheme.typography.bodyMedium, 
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            if (product.features.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = context.getString(R.string.feature_product_impl_features), 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        product.features.forEach { feature ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                                Text(text = feature, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = context.getString(R.string.feature_product_impl_description), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = context.getString(R.string.feature_product_impl_seller), 
                                style = MaterialTheme.typography.labelMedium, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = product.sellerName, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}
