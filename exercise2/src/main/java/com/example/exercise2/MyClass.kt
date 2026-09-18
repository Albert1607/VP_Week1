package com.example.exercise2

enum class element(val label: String) {
    FIRE("Fire"),
    WATER("Water"),
    GRASS("Grass");

    fun beats(other: element): Boolean {
        if (this == FIRE && other == GRASS) return true
        if (this == WATER && other == FIRE) return true
        if (this == GRASS && other == WATER) return true
        return false
    }
}

class Wizard(var name: String) {
    var maxHp = 50
    var hp = 50
    var maxMana = 30
    var mana = 30
    var kills = 0
    var killsToEvolve = 5
    var manaPotions = 5
    var healthPotions = 5
    var isStrong = false
    var lifesteal = 0

    fun getDamage(): Int {
        if (isStrong) return 15 else return 10
    }

    fun getManaCost(): Int {
        if (isStrong) return 15 else return 10
    }

    fun evolve() {
        if (isStrong == false && kills >= killsToEvolve) {
            isStrong = true
            lifesteal = 1
            maxHp = 75
            maxMana = 45
            hp = maxHp
            mana = maxMana
            println("\n*** $name evolved into a STRONG WIZARD! ***")
            println("*** Max HP: $maxHp | Max Mana: $maxMana | Lifesteal: $lifesteal ***\n")
        }
    }
}

class Monster(val type: element) {
    val name = type.label + "mon"
    var hp = 30
    val maxHp = 30
    val damage = 10
}

fun main() {
    println("Wizard Adventure!\n")
    println("You're a wizard that's been researching in a tower for several years...")

    println("\nWhat's your name?")
    var name = readLine()!!
    while (name == "") {
        println("Name cannot be empty. Try again:")
        name = readLine()!!
    }

    var wizard = Wizard(name)
    println("\nGood luck, ${wizard.name}!")

    while (true) {
        println("\nWhat're you going to do?")
        println("1. View Stats")
        println("2. Enter battle")
        println("3. Exit")
        val choice = readLine()!!

        if (choice == "1") {
            var back = false
            while (back == false) {
                println("\n--- STATS ---")
                println("HP: ${wizard.hp}/${wizard.maxHp}")
                println("Mana: ${wizard.mana}/${wizard.maxMana}")
                println("Kills: ${wizard.kills}/${wizard.killsToEvolve}")
                println("Potions: HP(${wizard.healthPotions}) MP(${wizard.manaPotions})")
                if (wizard.isStrong) println("Lifesteal: ${wizard.lifesteal}")
                
                println("\n1. Drink Mana Potion")
                println("2. Drink Health Potion")
                println("3. Rename self")
                println("0. Back")
                val statsChoice = readLine()!!

                if (statsChoice == "1") {
                    if (wizard.manaPotions > 0) {
                        wizard.manaPotions = wizard.manaPotions - 1
                        wizard.mana = wizard.mana + 15
                        if (wizard.mana > wizard.maxMana) wizard.mana = wizard.maxMana
                        println("Drank Mana Potion!")
                    } else {
                        println("No potions!")
                    }
                } else if (statsChoice == "2") {
                    if (wizard.healthPotions > 0) {
                        wizard.healthPotions = wizard.healthPotions - 1
                        wizard.hp = wizard.hp + 25
                        if (wizard.hp > wizard.maxHp) wizard.hp = wizard.maxHp
                        println("Drank Health Potion!")
                    } else {
                        println("No potions!")
                    }
                } else if (statsChoice == "3") {
                    println("New name:")
                    wizard.name = readLine()!!
                } else if (statsChoice == "0") {
                    back = true
                }
            }
        } else if (choice == "2") {
            val types = element.values()
            val monster = Monster(types.random())
            println("\nA wild ${monster.name} appears!")

            while (wizard.hp > 0 && monster.hp > 0) {
                println("\nWizard: HP ${wizard.hp}/${wizard.maxHp} | Mana ${wizard.mana}/${wizard.maxMana}")
                println("Monster: HP ${monster.hp}/${monster.maxHp}")
                println("1. Fire Attack")
                println("2. Water Attack")
                println("3. Grass Attack")
                println("4. Drink Potion")
                println("5. Run")
                val action = readLine()!!

                var wizardAttacked = false
                var spellType: element? = null

                if (action == "1") {
                    spellType = element.FIRE
                    wizardAttacked = true
                } else if (action == "2") {
                    spellType = element.WATER
                    wizardAttacked = true
                } else if (action == "3") {
                    spellType = element.GRASS
                    wizardAttacked = true
                } else if (action == "4") {
                    println("1. Health Potion | 2. Mana Potion | 0. Cancel")
                    val pot = readLine()!!
                    if (pot == "1") {
                        if (wizard.healthPotions > 0) {
                            wizard.healthPotions--
                            wizard.hp = minOf(wizard.maxHp, wizard.hp + 25)
                            println("Healed!")
                        }
                    } else if (pot == "2") {
                        if (wizard.manaPotions > 0) {
                            wizard.manaPotions--
                            wizard.mana = minOf(wizard.maxMana, wizard.mana + 15)
                            println("Restored Mana!")
                        }
                    }
                } else if (action == "5") {
                    println("Fled!")
                    break
                }

                if (wizardAttacked && spellType != null) {
                    val cost = wizard.getManaCost()
                    if (wizard.mana >= cost) {
                        wizard.mana = wizard.mana - cost
                        var dmg = wizard.getDamage()
                        if (spellType.beats(monster.type)) {
                            dmg = dmg * 2
                            println("Super Effective!")
                        }
                        monster.hp = monster.hp - dmg
                        println("You dealt $dmg damage!")
                        
                        if (wizard.isStrong) {
                            wizard.hp = minOf(wizard.maxHp, wizard.hp + wizard.lifesteal)
                            println("Lifesteal: +${wizard.lifesteal} HP")
                        }
                    } else {
                        println("Not enough mana!")
                        wizardAttacked = false
                    }
                }

                if (monster.hp <= 0) {
                    println("Monster defeated!")
                    wizard.kills = wizard.kills + 1
                    if (wizard.isStrong) wizard.lifesteal = wizard.lifesteal + 1
                    wizard.evolve()
                    break
                }

                if (wizardAttacked || action == "4") {
                    println("${monster.name} attacks for ${monster.damage} damage!")
                    wizard.hp = wizard.hp - monster.damage
                }

                if (wizard.hp <= 0) {
                    println("\nYou died! Restarting...")
                    wizard = Wizard(wizard.name)
                    break
                }
            }
        } else if (choice == "3") {
            println("Goodbye!")
            break
        }
    }
}
