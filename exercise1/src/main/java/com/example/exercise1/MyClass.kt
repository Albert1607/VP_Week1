package com.example.exercise1

data class MenuItem(
    var name: String,
    var description: String,
    var price: Double
)

data class OrderLine(
    val itemName: String,
    val quantity: Int,
    val lineTotal: Double
)

data class Order(
    val customerName: String,
    val lines: List<OrderLine>,
    val total: Double
)

fun main() {
    val menu = mutableListOf<MenuItem>()
    val orders = mutableListOf<Order>()

    // Starter items
    menu.add(MenuItem("Nasi Goreng", "Classic Indonesian fried rice", 25.0))
    menu.add(MenuItem("Es Teh Manis", "Sweet iced tea", 8.0))
    menu.add(MenuItem("Sate Ayam", "Grilled chicken skewers", 35.0))

    while (true) {
        println("\nORDER SYSTEM")
        println("1. Make order")
        println("2. View Orders")
        println("3. View Menu")
        println("4. Add Menu")
        println("5. Edit Menu")
        println("6. Delete Menu")
        println("7. Exit")
        println("Choose an option: ")
        
        val input = readLine()!!

        if (input == "1") {
            println("Enter customer's name: ")
            val customerName = readLine()!!
            val lines = mutableListOf<OrderLine>()
            var total = 0.0

            var done = "n"
            while (done == "n") {
                println("\n--- MENU ---")
                for (i in 0 until menu.size) {
                    println("${i + 1}. ${menu[i].name} - ${menu[i].price}")
                }
                println("Enter item number to add (or 0 to go back to menu): ")
                val menuChoice = readLine()!!.toInt()
                
                if (menuChoice == 0) {
                    done = "y"
                } else if (menuChoice > 0 && menuChoice <= menu.size) {
                    val item = menu[menuChoice - 1]
                    println("Enter quantity: ")
                    val qty = readLine()!!.toInt()
                    val linePrice = item.price * qty
                    lines.add(OrderLine(item.name, qty, linePrice))
                    total = total + linePrice
                    println("Added ${item.name}!")
                } else {
                    println("Invalid number!")
                }
            }
            if (lines.size > 0) {
                val newOrder = Order(customerName, lines, total)
                orders.add(newOrder)
                println("Order finished for $customerName")
            }

        } else if (input == "2") {
            println("\n--- VIEW ORDERS ---")
            if (orders.size == 0) {
                println("No orders yet.")
            } else {
                for (order in orders) {
                    println("Customer: ${order.customerName}")
                    for (line in order.lines) {
                        println("${line.itemName} x${line.quantity} = ${line.lineTotal}")
                    }
                    println("Total: ${order.total}")
                    println("------------------")
                }
            }

        } else if (input == "3") {
            println("\n--- MENU ---")
            for (item in menu) {
                println("${item.name} - ${item.price}")
                println("${item.description}")
            }

        } else if (input == "4") {
            println("Insert item name: ")
            val name = readLine()!!
            println("Insert description: ")
            val desc = readLine()!!
            println("Insert price: ")
            val price = readLine()!!.toDouble()
            menu.add(MenuItem(name, desc, price))
            println("Menu added!")

        } else if (input == "5") {
            println("\n--- MENU ---")
            for (i in 0 until menu.size) {
                println("${i + 1}. ${menu[i].name}")
            }
            println("Enter item number to edit: ")
            val choice = readLine()!!.toInt()
            
            if (choice > 0 && choice <= menu.size) {
                val item = menu[choice - 1]
                println("New name: ")
                item.name = readLine()!!
                println("New description: ")
                item.description = readLine()!!
                println("New price: ")
                item.price = readLine()!!.toDouble()
                println("Menu updated!")
            } else {
                println("Invalid number!")
            }

        } else if (input == "6") {
            println("\n--- MENU ---")
            for (i in 0 until menu.size) {
                println("${i + 1}. ${menu[i].name}")
            }
            println("Enter item number to delete: ")
            val choice = readLine()!!.toInt()
            
            if (choice > 0 && choice <= menu.size) {
                menu.removeAt(choice - 1)
                println("Menu deleted!")
            } else {
                println("Invalid number!")
            }

        } else if (input == "7") {
            println("Goodbye!")
            break
        } else {
            println("Menu not available")
        }
    }
}
