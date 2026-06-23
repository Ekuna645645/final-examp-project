package ge.btu.flowershop.data

import ge.btu.flowershop.data.model.Product

/**
 * Demo catalog shown before Firebase is connected, and used to seed Firestore from the
 * admin screen. Images are hotlinked from Unsplash's CDN.
 */
object DemoData {
    val products = listOf(
        Product(
            id = "demo-1",
            name = "Sunny Poppies",
            description = "A cheerful field-fresh bunch of golden California poppies. Brightens any room.",
            price = 24.99,
            imageUrl = "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=600&q=80",
            category = "Bouquets",
            stock = 12,
        ),
        Product(
            id = "demo-2",
            name = "Midnight Garden",
            description = "A dramatic premium arrangement of roses, ranunculus and anemone in deep jewel tones.",
            price = 49.99,
            imageUrl = "https://images.unsplash.com/photo-1457089328109-e5d9bd499191?w=600&q=80",
            category = "Premium",
            stock = 6,
        ),
        Product(
            id = "demo-3",
            name = "Blush Elegance",
            description = "Soft peonies and garden roses with eucalyptus, tied with a silk ribbon.",
            price = 39.99,
            imageUrl = "https://images.unsplash.com/photo-1563241527-3004b7be0ffd?w=600&q=80",
            category = "Bouquets",
            stock = 9,
        ),
        Product(
            id = "demo-4",
            name = "Pink Tulip Jar",
            description = "Fresh pink tulips arranged in a rustic glass jar. Simple and lovely.",
            price = 19.99,
            imageUrl = "https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=600&q=80",
            category = "Tulips",
            stock = 20,
        ),
        Product(
            id = "demo-5",
            name = "Candy Tulips",
            description = "Striking red-and-white striped tulips — a playful seasonal favourite.",
            price = 22.99,
            imageUrl = "https://images.unsplash.com/photo-1468327768560-75b778cbb551?w=600&q=80",
            category = "Tulips",
            stock = 15,
        ),
        Product(
            id = "demo-6",
            name = "With Love",
            description = "A heart-shaped mix of roses and daisies — the perfect romantic gesture.",
            price = 34.99,
            imageUrl = "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?w=600&q=80",
            category = "Romance",
            stock = 8,
        ),
    )
}
