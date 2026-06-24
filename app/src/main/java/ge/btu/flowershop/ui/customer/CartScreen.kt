package ge.btu.flowershop.ui.customer

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.ui.CartItem
import ge.btu.flowershop.ui.CartViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.ProductImage

@Composable
fun CartScreen(cartViewModel: CartViewModel, onCheckout: () -> Unit) {
    val items by cartViewModel.items.collectAsStateWithLifecycle()
    val total = items.sumOf { it.lineTotal }
    val count = items.sumOf { it.quantity }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Your cart", if (count == 0) "Empty" else "$count item(s)")

        if (items.isEmpty()) {
            EmptyState("Your cart is empty", "Add some flowers from the shop.", Icons.Filled.ShoppingCart)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(items, key = { it.product.id }) { item ->
                    CartRow(
                        item = item,
                        onQuantity = { qty -> cartViewModel.setQuantity(item.product.id, qty) },
                        onRemove = { cartViewModel.remove(item.product.id) },
                    )
                }
            }
            Surface(tonalElevation = 3.dp) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Total", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text(
                            "$%.2f".format(total),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                        Text("Checkout")
                    }
                    Text(
                        "Checkout uses Stripe test mode — no real charge.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartRow(item: CartItem, onQuantity: (Int) -> Unit, onRemove: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductImage(
                item.product.imageUrl,
                Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.product.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    "$%.2f".format(item.lineTotal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onQuantity(item.quantity - 1) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                    }
                    Text("${item.quantity}", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { onQuantity(item.quantity + 1) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove")
            }
        }
    }
}
