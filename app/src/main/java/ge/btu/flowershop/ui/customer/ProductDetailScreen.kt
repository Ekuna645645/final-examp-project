package ge.btu.flowershop.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.Product
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.components.ProductImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product?,
    onBack: () -> Unit,
    onAddToCart: (Product, Int) -> Unit,
) {
    if (product == null) {
        EmptyState("Product not found", "It may have been removed.")
        return
    }
    var quantity by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            ProductImage(product.imageUrl, Modifier.fillMaxWidth().height(280.dp))
            Column(Modifier.padding(20.dp)) {
                Text(product.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    product.priceLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                AssistChip(onClick = {}, label = { Text(product.category) })
                Spacer(Modifier.height(16.dp))
                Text(product.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantity", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    OutlinedIconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease")
                    }
                    Text(
                        "$quantity",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    OutlinedIconButton(onClick = { quantity++ }) {
                        Icon(Icons.Filled.Add, contentDescription = "Increase")
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { onAddToCart(product, quantity) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Add to cart · $%.2f".format(product.price * quantity))
                }
            }
        }
    }
}
