package com.yfy.basearchitecture.feature.cart.impl.mvc.ui.checkout

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yfy.basearchitecture.core.ui.base.mvc.BaseScreenScaffoldMvc
import com.yfy.basearchitecture.feature.cart.api.model.Address
import com.yfy.basearchitecture.feature.cart.api.model.CartItem
import com.yfy.basearchitecture.feature.cart.api.model.CheckoutStep
import com.yfy.basearchitecture.feature.cart.api.CartNavigation

@Composable
fun CheckoutScreen(
    navigation: CartNavigation
) {
    val model = hiltViewModel<CheckoutModelWrapper>().model
    val controller = remember { 
        CheckoutController(
            model = model,
            navigation = navigation
        )
    }
    val state = model.state.collectAsState().value
    
    BaseScreenScaffoldMvc(
        controller = controller,
        screenName = "CheckoutScreen",
        isLoading = state.isLoading
    ) {
        CheckoutContent(
            state = state,
            onAddressSelected = controller::onAddressSelected,
            onPaymentSelected = controller::onPaymentSelected,
            onPreviousStep = controller::onPreviousStep,
            onNextStep = controller::onNextStep,
            onPlaceOrder = controller::onPlaceOrder,
            onBackClick = controller::onBackClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutContent(
    state: CheckoutState, 
    onAddressSelected: (String) -> Unit, 
    onPaymentSelected: (String) -> Unit, 
    onPreviousStep: () -> Unit, 
    onNextStep: () -> Unit, 
    onPlaceOrder: () -> Unit, 
    onBackClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ödeme [${state.currentStep.ordinal + 1}/3]") },
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
                    modifier = Modifier.fillMaxWidth().padding(16.dp), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.currentStep != CheckoutStep.ADDRESS) {
                        OutlinedButton(onClick = onPreviousStep, modifier = Modifier.weight(1f)) { 
                            Text("Geri") 
                        }
                    }
                    Button(
                        onClick = { 
                            if (state.currentStep == CheckoutStep.CONFIRMATION) onPlaceOrder() else onNextStep()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.canProceed
                    ) {
                        Text(
                            if (state.currentStep == CheckoutStep.CONFIRMATION) "Siparişi Onayla" else "Devam Et"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp), 
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StepIndicator(
                    label = "Adres", 
                    isActive = state.currentStep == CheckoutStep.ADDRESS,
                    isCompleted = state.currentStep.ordinal > CheckoutStep.ADDRESS.ordinal
                )
                StepIndicator(
                    label = "Ödeme", 
                    isActive = state.currentStep == CheckoutStep.PAYMENT,
                    isCompleted = state.currentStep.ordinal > CheckoutStep.PAYMENT.ordinal
                )
                StepIndicator(
                    label = "Özet", 
                    isActive = state.currentStep == CheckoutStep.CONFIRMATION,
                    isCompleted = false
                )
            }
            Divider()
            when (state.currentStep) {
                CheckoutStep.ADDRESS -> AddressStep(
                    addresses = state.addresses, 
                    selectedAddressId = state.selectedAddress?.id, 
                    onAddressSelected = onAddressSelected
                )
                CheckoutStep.PAYMENT -> PaymentStep(
                    selectedPayment = state.selectedPayment, 
                    onPaymentSelected = onPaymentSelected
                )
                CheckoutStep.CONFIRMATION -> ConfirmationStep(
                    address = state.selectedAddress, 
                    paymentMethod = state.selectedPayment, 
                    items = state.cartItems, 
                    subtotal = state.subtotal, 
                    shippingCost = state.shippingCost, 
                    total = state.total
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(label: String, isActive: Boolean, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(
                when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    isActive -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Check, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onPrimary, 
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Box(
                    modifier = Modifier.size(12.dp).clip(CircleShape).background(
                        if (isActive) MaterialTheme.colorScheme.onPrimary 
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddressStep(
    addresses: List<Address>, 
    selectedAddressId: String?, 
    onAddressSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), 
        contentPadding = PaddingValues(16.dp), 
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Teslimat Adresi Seçin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(addresses, key = { it.id }) { address ->
            AddressCard(
                address = address, 
                isSelected = address.id == selectedAddressId, 
                onClick = { onAddressSelected(address.id) }
            )
        }
    }
}

@Composable
private fun AddressCard(
    address: Address, 
    isSelected: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = address.title, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = address.fullName, style = MaterialTheme.typography.bodyMedium)
                Text(text = address.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${address.city}, ${address.district}", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = address.addressLine,
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PaymentStep(
    selectedPayment: String?, 
    onPaymentSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp), 
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ödeme Yöntemi Seçin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        PaymentMethodCard(
            label = "💳 Kredi Kartı", 
            isSelected = selectedPayment == "credit_card", 
            onClick = { onPaymentSelected("credit_card") }
        )
        PaymentMethodCard(
            label = "🏦 Havale/EFT", 
            isSelected = selectedPayment == "bank_transfer", 
            onClick = { onPaymentSelected("bank_transfer") }
        )
        PaymentMethodCard(
            label = "📦 Kapıda Ödeme", 
            isSelected = selectedPayment == "cash_on_delivery", 
            onClick = { onPaymentSelected("cash_on_delivery") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "(Bu bir mock ödeme ekranıdır)", 
            style = MaterialTheme.typography.bodySmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PaymentMethodCard(
    label: String, 
    isSelected: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ConfirmationStep(
    address: Address?, 
    paymentMethod: String?, 
    items: List<CartItem>, 
    subtotal: Double, 
    shippingCost: Double, 
    total: Double
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), 
        contentPadding = PaddingValues(16.dp), 
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Sipariş Özeti", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        if (address != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📍 Teslimat Adresi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(address.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(address.fullName, style = MaterialTheme.typography.bodyMedium)
                        Text("${address.city}, ${address.district}", style = MaterialTheme.typography.bodySmall)
                        Text(address.addressLine, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        if (paymentMethod != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💳 Ödeme Yöntemi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (paymentMethod) {
                                "credit_card" -> "Kredi Kartı"
                                "bank_transfer" -> "Havale/EFT"
                                "cash_on_delivery" -> "Kapıda Ödeme"
                                else -> paymentMethod
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        item {
            Text("📦 Sipariş Detayları", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(items, key = { it.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = item.productImage, 
                        contentDescription = item.productName, 
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), 
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName, 
                            style = MaterialTheme.typography.bodyMedium, 
                            maxLines = 2, 
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Adet: ${item.quantity}", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${item.price * item.quantity} TL", 
                        style = MaterialTheme.typography.titleSmall, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ürünler Toplamı", style = MaterialTheme.typography.bodyMedium)
                        Text("$subtotal TL", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kargo", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            if (shippingCost == 0.0) "Ücretsiz" else "$shippingCost TL", 
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Toplam", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "$total TL", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
