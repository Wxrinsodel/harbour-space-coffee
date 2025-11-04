package space.harbour.coffee.menu

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicInteger

@RestController
@RequestMapping("/api/menu")
class MenuController {

    // Stores the menu items in memory
    private val menuItems: MutableList<MenuItem> = mutableListOf()
    private val idCounter = AtomicInteger(0)

    // Initialize with some default items for testing
    init {
        createItem(MenuItem(0, "Latte", 4.50, "Classic espresso with steamed milk and a thin layer of foam.", "Coffee", true))
        createItem(MenuItem(0, "Earl Grey", 3.00, "Black tea flavored with bergamot.", "Tea", true))
        createItem(MenuItem(0, "Chocolate Croissant", 3.50, "Flaky pastry filled with chocolate.", "Pastry", false))
    }

    private fun createItem(item: MenuItem): MenuItem {
        val newId = idCounter.incrementAndGet()
        // Create a new item with the generated ID, ignoring the ID passed in the request body
        val newItem = item.copy(id = newId)
        menuItems.add(newItem)
        return newItem
    }

    @GetMapping
    fun getAllMenuItems(): ResponseEntity<List<MenuItem>> {
        return ResponseEntity.ok(menuItems)
    }

    @GetMapping("/{id}")
    fun getMenuItemDetails(@PathVariable id: Int): ResponseEntity<MenuItem> {
        // Find the item by its ID
        val item = menuItems.find { it.id == id }

        // Handle the edge case: item not found
        return if (item != null) {
            ResponseEntity.ok(item) // 200 OK
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build() // 404 Not Found
        }
    }

    @PostMapping
    fun addMenuItem(@RequestBody item: MenuItem): ResponseEntity<MenuItem> {
        // Validate basic input
        if (item.name.isBlank() || item.price <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build() // 400 Bad Request
        }

        // Use the helper function to generate a unique ID and store the item
        val newItem = createItem(item)

        // Return 201 Created and the newly created object
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem)
    }

    @PutMapping("/{id}")
    fun updateMenuItem(@PathVariable id: Int, @RequestBody updatedItem: MenuItem): ResponseEntity<MenuItem> {
        // Find the index of the existing item
        val index = menuItems.indexOfFirst { it.id == id }

        if (index == -1) {
            // Handle the edge case: item not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build() // 404 Not Found
        }

        // Create a new MenuItem object with the original ID but updated properties
        val itemToUpdate = updatedItem.copy(id = id)

        // Replace the old item with the updated item in the list
        menuItems[index] = itemToUpdate

        // Return 200 OK with the updated object
        return ResponseEntity.ok(itemToUpdate)
    }

    // --- 5. Delete a menu item (DELETE /api/menu/{id}) ---
    @DeleteMapping("/{id}")
    fun deleteMenuItem(@PathVariable id: Int): ResponseEntity<Void> {
        // Use the removeIf function which returns 'true' if an element was removed
        val removed = menuItems.removeIf { it.id == id }

        return if (removed) {
            // Return 204 No Content for a successful deletion
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } else {
            // Handle the edge case: item not found
            ResponseEntity.status(HttpStatus.NOT_FOUND).build() // 404 Not Found
        }
    }
}