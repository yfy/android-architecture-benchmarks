package com.yfy.basearchitecture.feature.product.impl.mvi.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yfy.basearchitecture.core.ui.api.base.BaseScreenScaffold
import com.yfy.basearchitecture.feature.product.api.model.Product
import com.yfy.basearchitecture.feature.product.impl.mvi.R

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = hiltViewModel()
) {
    BaseScreenScaffold(
        viewModel = viewModel,
        screenName = "ProductListScreen"
    ) {
        val state = viewModel.state.collectAsState().value
        ProductListContent(
            state = state,
            onProductClick = { viewModel.handleIntent(ProductListIntent.ProductClicked(it)) },
            onCategoryClick = { viewModel.handleIntent(ProductListIntent.CategorySelected(it)) },
            onLoadMore = { viewModel.handleIntent(ProductListIntent.LoadMore) },
            onRefresh = { viewModel.handleIntent(ProductListIntent.Refresh) },
            onCartClick = { viewModel.handleIntent(ProductListIntent.CartClicked) },
            onChatClick = { viewModel.handleIntent(ProductListIntent.ChatClicked) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListContent(
    state: ProductListState,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onCartClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(context.getString(R.string.feature_product_impl_product_list_title)) },
            actions = {
                IconButton(onClick = onChatClick) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Message Box"
                    )
                }
                BadgedBox(
                    badge = {
                        if (state.cartItemCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (state.cartItemCount > 9) "9+" else state.cartItemCount.toString(),
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shopping Cart"
                        )
                    }
                }
            }
        )
        
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(state.categories) { category ->
                FilterChip(
                    selected = state.selectedCategoryId == category.id,
                    onClick = { onCategoryClick(category.id) },
                    label = { Text("${category.icon} ${category.name}") }
                )
            }
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.products, key = { it.id }) { product ->
                ProductCard(product = product, onClick = { onProductClick(product.id) })
            }
            
            if (state.hasMore && !state.isLoadingMore) {
                item(span = { GridItemSpan(2) }) {
                    LaunchedEffect(Unit) { onLoadMore() }
                }
            }
            
            if (state.isLoadingMore) {
                item(span = { GridItemSpan(2) }) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        if (state.products.isEmpty() && state.isLoading.not() && state.error.isNullOrEmpty().not()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(context.getString(R.string.feature_product_impl_error_loading_products))
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        onClick = onClick, 
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name, 
                    style = MaterialTheme.typography.bodyMedium, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${product.discountPrice ?: product.price} TL", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    )
                    if (product.discountPrice != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.price} TL", 
                            style = MaterialTheme.typography.bodySmall, 
                            textDecoration = TextDecoration.LineThrough, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star, 
                            contentDescription = null, 
                            tint = Color(0xFFFFA000), 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = product.rating.toString(), style = MaterialTheme.typography.bodySmall)
                    }
                    if (product.freeShipping) {
                        Text(
                            text = context.getString(R.string.feature_product_impl_free_shipping), 
                            style = MaterialTheme.typography.labelSmall, 
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
