package com.example.exercise

enum class ElementType(val label: String) {
    FIRE("Fire"),
    WATER("Water"),
    GRASS("Grass");

    /** Returns true if this type beats [other] (deals 2x damage against it). */
    fun beats(other: ElementType): Boolean = when (this) {
        FIRE -> other == GRASS
        WATER -> other == FIRE
        GRASS -> other == WATER
    }
}

class Wizard(var name: String) {
    var maxHp: Int = 50
    var hp: Int = 50
    var maxMana: Int = 30
    var mana: Int = 30
    var kills: Int = 0
    val killsToEvolve: Int = 5
    var manaPotions: Int = 5
    var healthPotions: Int = 5
    var isStrong: Boolean = false
    var lifesteal: Int = 0

    val spellDamage: Int
        get() = if (isStrong) (10 * 1.5).toInt() else 10

    val spellManaCost: Int
        get() = if (isStrong) (10 * 1.5).toInt() else 10

    fun evolveIfReady() {
        if (!isStrong && kills >= killsToEvolve) {
            isStrong = true
            lifesteal = 1
            maxHp = (50 * 1.5).toInt()
            maxMana = (30 * 1.5).toInt()
            hp = maxHp
            mana = maxMana
            println("\n*** $name absorbed enough essence and evolved into a STRONG WIZARD! ***")
            println("*** Max HP: $maxHp | Max Mana: $maxMana | Lifesteal: $lifesteal ***\n")
        }
    }

    fun registerKill() {
        kills++
        if (isStrong) {
            lifesteal++
        }
        evolveIfReady()
    }

    fun drinkManaPotion(): Boolean {
        if (manaPotions <= 0) {
            println("You have no Mana Potions left!")
            return false
        }
        manaPotions--
        mana = minOf(maxMana, mana + 15)
        println("$name drank a Mana Potion. Mana: $mana/$maxMana")
        return true
    }

    fun drinkHealthPotion(): Boolean {
        if (healthPotions <= 0) {
            println("You have no Health Potions left!")
            return false
        }
        healthPotions--
        hp = minOf(maxHp, hp + 25)
        println("$name drank a Health Potion. HP: $hp/$maxHp")
        return true
    }

    fun isAlive(): Boolean = hp > 0

    fun resetForNewRun() {
        hp = maxHp
        mana = maxMana
        // Potions, kills, and evolution persist per the "IF DIE ULANG" (restart) rule below,
        // which restarts the whole app state — see main().
    }

    fun printStats() {
        println("\n——— $name's STATS ———")
        println("HP: $hp/$maxHp")
        println("Mana: $mana/$maxMana")
        println(
            "Kills needed to evolve: ${
                minOf(
                    kills,
                    killsToEvolve
                )
            }/$killsToEvolve" + if (isStrong) " (EVOLVED)" else ""
        )
        println("Mana Potions held: $manaPotions")
        println("Health Potions held: $healthPotions")
        if (isStrong) println("Lifesteal: $lifesteal")
        println("——————————————————————")
    }
}

class Monster(val type: ElementType) {
    val name: String = type.label + "mon"
    val maxHp: Int = 30
    var hp: Int = maxHp
    val attackDamage: Int = 10

    fun isAlive(): Boolean = hp > 0

    fun printStats() {
        println("\n$name")
        println("HP: $hp/$maxHp")
        println("Type: ${type.label}")
    }
}

class WizardAdventureGame {
    private val reader = System.`in`.bufferedReader()
    private lateinit var wizard: Wizard

    fun start() {
        println("GAVIN")
        println("Wizard Adventure!\n")
        println(
            "You're a wizard that's been researching in a tower for several years when you\n" +
                    "find out that after killing enough enemies and absorbing their essence, you can\n" +
                    "evolve into a stronger wizard and develop an attribute that can make yourself\n" +
                    "immortal... With this revelation in mind, you venture off into the nearby forest\n" +
                    "in hopes of evolving yourself..."
        )

        runNewGame()
    }

    private fun runNewGame() {
        val name = promptNonEmpty("\nWhat's your name?")
        wizard = Wizard(name)
        println("\nGood luck, ${wizard.name}! You're gonna need it!")
        appFlowLoop()
    }

    private fun appFlowLoop() {
        while (true) {
            println("\nWhat're you going to do?")
            println("1. View Stats")
            println("2. Enter battle")
            println("3. Exit")

            when (readIntInRange("Choose an option:", 1..3)) {
                1 -> viewStatsMenu()
                2 -> enterBattle()
                3 -> {
                    println("Farewell, ${wizard.name}.")
                    return
                }
            }
        }
    }

    private fun viewStatsMenu() {
        while (true) {
            wizard.printStats()
            println("a. Drink Mana Potion")
            println("b. Drink Health Potion")
            println("c. Rename self")
            println("d. Back")

            when (readLetterInSet("Choose an option:", setOf('a', 'b', 'c', 'd'))) {
                'a' -> wizard.drinkManaPotion()
                'b' -> wizard.drinkHealthPotion()
                'c' -> {
                    val newName = promptNonEmpty("Enter new name:")
                    wizard.name = newName
                    println("Your name is now ${wizard.name}.")
                }

                'd' -> return
            }
        }
    }

    private fun enterBattle() {
        val type = ElementType.values().random()
        val monster = Monster(type)

        println("\n——— BATTLE ———")
        println("A wild ${monster.name} appears!")

        while (wizard.isAlive() && monster.isAlive()) {
            printBattleStatus(monster)

            println("a. Fire Attack")
            println("b. Water Attack")
            println("c. Grass Attack")
            println("d. Drink potion")
            println("e. Run")

            when (readLetterInSet("Choose an action:", setOf('a', 'b', 'c', 'd', 'e'))) {
                'a' -> castSpell(ElementType.FIRE, monster)
                'b' -> castSpell(ElementType.WATER, monster)
                'c' -> castSpell(ElementType.GRASS, monster)
                'd' -> battlePotionMenu()
                'e' -> {
                    println("${wizard.name} fled the battle!")
                    return
                }
            }

            if (!monster.isAlive()) {
                println("\n${monster.name} was defeated!")
                wizard.registerKill()
                break
            }

            // Monster's turn
            monster.attack(wizard)

            if (!wizard.isAlive()) {
                handleDefeat()
                return
            }
        }
    }

    private fun printBattleStatus(monster: Monster) {
        println("\n${wizard.name}")
        println("HP: ${wizard.hp}/${wizard.maxHp}")
        println("Mana: ${wizard.mana}/${wizard.maxMana}")
        println("HP Potions: ${wizard.healthPotions}")
        println("MP Potions: ${wizard.manaPotions}")
        monster.printStats()
    }

    private fun castSpell(spellType: ElementType, monster: Monster) {
        if (wizard.mana < wizard.spellManaCost) {
            println("Not enough mana! You need ${wizard.spellManaCost} mana.")
            return
        }
        wizard.mana -= wizard.spellManaCost

        var damage = wizard.spellDamage
        val effective = spellType.beats(monster.type)
        if (effective) damage *= 2

        monster.hp = maxOf(0, monster.hp - damage)
        println(
            "${wizard.name} casts ${spellType.label} Attack for $damage damage" +
                    (if (effective) " (super effective!)" else "") + "!"
        )

        if (!monster.isAlive()) return

        if (wizard.isStrong && wizard.lifesteal > 0) {
            wizard.hp = minOf(wizard.maxHp, wizard.hp + wizard.lifesteal)
            println("${wizard.name} drains ${wizard.lifesteal} HP from lifesteal! HP: ${wizard.hp}/${wizard.maxHp}")
        }
    }

    private fun battlePotionMenu() {
        println("a. Drink Mana Potion")
        println("b. Drink Health Potion")
        println("c. Cancel")
        when (readLetterInSet("Choose a potion:", setOf('a', 'b', 'c'))) {
            'a' -> wizard.drinkManaPotion()
            'b' -> wizard.drinkHealthPotion()
            'c' -> { /* no-op, cancel */
            }
        }
    }

    private fun Monster.attack(target: Wizard) {
        target.hp = maxOf(0, target.hp - this.attackDamage)
        println("${this.name} attacks ${target.name} for ${this.attackDamage} damage! HP: ${target.hp}/${target.maxHp}")
    }

    private fun handleDefeat() {
        println("\n${wizard.name} has fallen...")
        println("IF DIE ULANG — restarting the adventure!\n")
        runNewGame()
    }

    // ---------- Input helpers with error handling ----------

    private fun promptNonEmpty(message: String): String {
        while (true) {
            println(message)
            print("<Insert name> ")
            val line = readLine()?.trim()
            if (!line.isNullOrEmpty()) return line
            println("Name cannot be empty. Please try again.")
        }
    }

    private fun readIntInRange(message: String, range: IntRange): Int {
        while (true) {
            println(message)
            val input = readLine()?.trim()
            val value = input?.toIntOrNull()
            if (value != null && value in range) return value
            println("Invalid input. Please enter a number between ${range.first} and ${range.last}.")
        }
    }

    private fun readLetterInSet(message: String, allowed: Set<Char>): Char {
        while (true) {
            println(message)
            @Suppress("DEPRECATION")
            val input = readLine()?.trim()?.toLowerCase()
            if (input != null && input.length == 1 && input[0] in allowed) return input[0]
            println("Invalid input. Please enter one of: ${allowed.joinToString(", ")}.")
        }
    }
}

fun main() {
    try {
        WizardAdventureGame().start()
    } catch (e: Exception) {
        println("An unexpected error occurred: ${e.message}")
        println("The adventure has ended unexpectedly.")
    }
}
