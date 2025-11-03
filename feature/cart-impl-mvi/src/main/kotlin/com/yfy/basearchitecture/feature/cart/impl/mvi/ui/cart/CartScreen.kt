package com.yfy.basearchitecture.feature.cart.impl.mvi.ui.cart

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yfy.basearchitecture.core.ui.api.base.BaseScreenScaffold
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.impl.mvi.R

@Composable
fun CartScreen(viewModel: CartViewModel = hiltViewModel()) {
    BaseScreenScaffold(viewModel = viewModel, screenName = "CartScreen") {
        val state = viewModel.state.collectAsState().value
        CartContent(
            state = state,
            onUpdateQuantity = { itemId, quantity -> viewModel.handleIntent(CartIntent.UpdateQuantity(itemId, quantity)) },
            onRemoveItem = { itemId -> viewModel.handleIntent(CartIntent.RemoveItem(itemId)) },
            onCheckout = { viewModel.handleIntent(CartIntent.NavigateToCheckout) },
            onBackClick = { viewModel.handleIntent(CartIntent.NavigateBack) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartContent(
    state: CartState, 
    onUpdateQuantity: (String, Int) -> Unit, 
    onRemoveItem: (String) -> Unit, 
    onCheckout: () -> Unit, 
    onBackClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text(context.getString(R.string.feature_cart_impl_cart_title)) },
                navigationIcon = { 
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") 
                    } 
                }
            ) 
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Surface {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(context.getString(R.string.feature_cart_impl_total), style = MaterialTheme.typography.bodyMedium)
                            Text("${state.subtotal.toInt()} TL", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Shipping", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                if (state.shippingCost == 0.0) "Free" else "${state.shippingCost} TL", 
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(context.getString(R.string.feature_cart_impl_total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "${state.total.toInt()} TL",
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Bold, 
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                            Text(context.getString(R.string.feature_cart_impl_proceed_to_checkout))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(context.getString(R.string.feature_cart_impl_empty_cart), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add products to start shopping", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(paddingValues), 
                contentPadding = PaddingValues(16.dp), 
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = { it.id }) { item ->
                    CartItemCard(
                        item = item, 
                        onUpdateQuantity = { quantity -> onUpdateQuantity(item.id, quantity) }, 
                        onRemove = { onRemoveItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem, 
    onUpdateQuantity: (Int) -> Unit, 
    onRemove: () -> Unit, 
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.productImage, 
                contentDescription = item.productName, 
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)), 
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName, 
                    style = MaterialTheme.typography.bodyMedium, 
                    fontWeight = FontWeight.Medium, 
                    maxLines = 2, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.sellerName, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${item.price} TL", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onUpdateQuantity((item.quantity - 1).coerceAtLeast(1)) }, 
                        modifier = Modifier.size(32.dp)
                    ) { 
                        Icon(Icons.Default.Remove, contentDescription = "Decrease") 
                    }
                    Text(
                        text = item.quantity.toString(), 
                        style = MaterialTheme.typography.bodyMedium, 
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) }, 
                        modifier = Modifier.size(32.dp)
                    ) { 
                        Icon(Icons.Default.Add, contentDescription = "Increase") 
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onRemove) { 
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Remove", 
                            tint = MaterialTheme.colorScheme.error
                        ) 
                    }
                }
            }
        }
    }
}
