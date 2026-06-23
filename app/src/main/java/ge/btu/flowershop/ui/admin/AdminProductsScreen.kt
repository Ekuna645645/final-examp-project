package ge.btu.flowershop.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.Product
import ge.btu.flowershop.ui.ProductViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.FirebaseWarningBanner
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.ProductImage

@Composable
fun AdminProductsScreen(
    productViewModel: ProductViewModel,
    onEdit: (Product) -> Unit,
) {
    val products by productViewModel.products.collectAsStateWithLifecycle()
    var toDelete by remember { mutableStateOf<Product?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Products", "${products.size} item(s)")

        if (!productViewModel.isConfigured) {
            FirebaseWarningBanner(Modifier.padding(horizontal = 16.dp))
            Text(
                "These demo products are read-only until Firebase is connected.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
        }

        if (products.isEmpty()) {
            EmptyState(
                "No products yet",
                if (productViewModel.isConfigured) "Tap + to add one, or seed the demo catalog from the editor."
                else "Connect Firebase to add products.",
                Icons.Filled.Inventory2,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(products, key = { it.id }) { product ->
                    AdminProductRow(
                        product = product,
                        onClick = { onEdit(product) },
                        onDelete = { toDelete = product },
                    )
                }
            }
        }
    }

    toDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Delete product?") },
            text = { Text("\"${product.name}\" will be removed from the catalog.") },
            confirmButton = {
                TextButton(onClick = {
                    productViewModel.delete(product.id)
                    toDelete = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun AdminProductRow(product: Product, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductImage(product.imageUrl, Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    "${product.priceLabel}  ·  stock ${product.stock}${if (!product.active) "  ·  hidden" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete")
            }
        }
    }
}
