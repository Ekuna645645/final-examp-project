package ge.btu.flowershop.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.data.model.OrderItem
import ge.btu.flowershop.data.model.OrderStatus
import ge.btu.flowershop.ui.CartItem
import ge.btu.flowershop.ui.OrderViewModel

/**
 * Checkout: order summary + delivery details + a (mock) Stripe-test card form. Paying
 * places the order with paymentStatus = PAID. Swap the mock block for Stripe PaymentSheet
 * once the publishable key + backend are wired.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    user: AppUser,
    orderViewModel: OrderViewModel,
    savedAddresses: List<String>,
    onSaveAddress: (String) -> Unit,
    onBack: () -> Unit,
    onPlaced: () -> Unit,
) {
    val total = items.sumOf { it.lineTotal }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var card by remember { mutableStateOf("4242 4242 4242 4242") }
    var expiry by remember { mutableStateOf("12 / 34") }
    var cvc by remember { mutableStateOf("123") }
    var processing by remember { mutableStateOf(false) }
    var saveAddress by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Order summary", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    items.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text("${item.quantity}×  ${item.product.name}", Modifier.weight(1f))
                            Text("$%.2f".format(item.lineTotal))
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text("Total", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text(
                            "$%.2f".format(total),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Text("Delivery details", style = MaterialTheme.typography.titleMedium)
            if (savedAddresses.isNotEmpty()) {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    savedAddresses.forEach { saved ->
                        FilterChip(
                            selected = address == saved,
                            onClick = { address = saved },
                            label = { Text(saved) },
                        )
                    }
                }
            }
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Delivery address") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = saveAddress, onCheckedChange = { saveAddress = it })
                Text("Save this address for next time")
            }
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Payment", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = card,
                onValueChange = { card = it },
                label = { Text("Card number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = { expiry = it },
                    label = { Text("MM/YY") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = cvc,
                    onValueChange = { cvc = it },
                    label = { Text("CVC") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                "Stripe test mode — prefilled with test card 4242 4242 4242 4242, no real charge. " +
                    "Real Stripe keys + backend slot in here later.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    processing = true
                    error = null
                    if (saveAddress) onSaveAddress(address.trim())
                    orderViewModel.placeOrder(
                        Order(
                            customerId = user.uid,
                            customerName = user.name.ifBlank { user.email },
                            items = items.map {
                                OrderItem(
                                    productId = it.product.id,
                                    name = it.product.name,
                                    price = it.product.price,
                                    quantity = it.quantity,
                                    imageUrl = it.product.imageUrl,
                                )
                            },
                            total = total,
                            status = OrderStatus.PLACED.name,
                            deliveryAddress = address.trim(),
                            phone = phone.trim(),
                            paymentStatus = "PAID",
                        ),
                    ) { success ->
                        if (success) {
                            onPlaced()
                        } else {
                            processing = false
                            error = "Couldn't place the order. Check your connection and try again."
                        }
                    }
                },
                enabled = !processing && items.isNotEmpty() && address.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (processing) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Pay  $%.2f".format(total))
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
