package ge.btu.flowershop.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.Product
import ge.btu.flowershop.data.model.productCategories
import ge.btu.flowershop.ui.components.ProductImage

/** Create or edit a product. Preserves id/createdAt when editing. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    existing: Product?,
    onSave: (Product) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var priceText by remember { mutableStateOf(existing?.price?.takeIf { it > 0 }?.toString() ?: "") }
    var stockText by remember { mutableStateOf(existing?.stock?.toString() ?: "0") }
    var imageUrl by remember { mutableStateOf(existing?.imageUrl ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: productCategories.first()) }
    var active by remember { mutableStateOf(existing?.active ?: true) }

    val price = priceText.toDoubleOrNull()
    val valid = name.isNotBlank() && price != null && price > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "New product" else "Edit product") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (imageUrl.isNotBlank()) {
                ProductImage(imageUrl, Modifier.fillMaxWidth().height(160.dp))
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }

            Text("Category", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(productCategories) { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        label = { Text(option) },
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Visible in shop", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Switch(checked = active, onCheckedChange = { active = it })
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    val product = (existing ?: Product()).copy(
                        name = name.trim(),
                        description = description.trim(),
                        price = price ?: 0.0,
                        stock = stockText.toIntOrNull() ?: 0,
                        imageUrl = imageUrl.trim(),
                        category = category,
                        active = active,
                    )
                    onSave(product)
                },
                enabled = valid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (existing == null) "Create product" else "Save changes")
            }
        }
    }
}
