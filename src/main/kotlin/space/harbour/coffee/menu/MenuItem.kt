package space.harbour.coffee.menu

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    var available: Boolean = true
)