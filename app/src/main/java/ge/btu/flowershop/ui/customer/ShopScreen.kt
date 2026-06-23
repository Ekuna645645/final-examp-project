package ge.btu.flowershop.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.Product
import ge.btu.flowershop.ui.ProductViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.ProductCard

@Composable
fun ShopScreen(
    productViewModel: ProductViewModel,
    onProductClick: (Product) -> Unit,
    onAdd: (Product) -> Unit,
) {
    val products by productViewModel.products.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val visible = products.filter { it.active }
    val categories = remember(visible) { visible.map { it.category }.distinct() }
    val filtered = if (selectedCategory == null) visible else visible.filter { it.category == selectedCategory }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Bloom", "Fresh flowers for every occasion")

        if (!productViewModel.isConfigured) {
            Text(
                "Showing demo flowers — connect Firebase to manage real products.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        if (categories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All") },
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            EmptyState("No flowers yet", "Check back soon for fresh arrivals.", Icons.Filled.LocalFlorist)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(filtered, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onAdd = { onAdd(product) },
                    )
                }
            }
        }
    }
}
