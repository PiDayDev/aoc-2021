package y15

import kotlin.math.max
import kotlin.math.min

private const val day = "22"

private data class Character(val hp: Int, val damage: Int = 0, val armor: Int = 0, val mana: Int = 0) {
    val isAlive = hp > 0
}

private data class Spell(
    val name: String,
    val manaCost: Int,
    val turns: Int = 0,
    val restoreHp: Int = 0,
    val dealDamage: Int = 0,
    val setArmor: Int = 0,
    val addMana: Int = 0
) {
    fun onPlayer(p: Character) = p.copy(hp = p.hp + restoreHp, armor = max(p.armor, setArmor), mana = p.mana + addMana)
    fun onBoss(b: Character) = b.copy(hp = b.hp - dealDamage)
}

private data class State(
    val player: Character, val boss: Character, val effects: Map<Spell, Int> = mapOf()
) {
    val spells = listOf(
        Spell(name = "Magic Missile", manaCost = 53, dealDamage = 4),
        Spell(name = "Drain", manaCost = 73, dealDamage = 2, restoreHp = 2),
        Spell(name = "Shield", manaCost = 113, turns = 6, setArmor = 7),
        Spell(name = "Poison", manaCost = 173, turns = 6, dealDamage = 3),
        Spell(name = "Recharge", manaCost = 229, turns = 5, addMana = 101),
    )


    fun playerWins() = player.isAlive && !boss.isAlive
    fun playerLoses() = !player.isAlive
    fun ends() = playerWins() || playerLoses()

    fun applyEffects() = with(effects) {
        State(
            player = keys.fold(player.copy(armor = 0)) { p, s -> s.onPlayer(p) },
            boss = keys.fold(boss) { b, s -> s.onBoss(b) },
            effects = mapValues { (_, v) -> v - 1 }.filterValues { it > 0 }
        )
    }

    private fun applyPenalty(hard: Boolean) = if (hard) copy(player = player.copy(hp = player.hp - 1)) else this

    fun cast(spell: Spell, hard: Boolean): State {
        // player's turn
        val initial = applyPenalty(hard)
        if (initial.ends()) return initial

        var (player1, boss1, effects1) = initial.applyEffects()
        check(spell !in effects1) { "Effect ${spell.name} already active" }

        player1 = player1.copy(mana = player1.mana - spell.manaCost)
        when (spell.turns) {
            0 -> {
                boss1 = spell.onBoss(boss1)
                player1 = spell.onPlayer(player1)
            }
            else -> effects1 += spell to spell.turns
        }
        check(player1.mana >= 0) { "Not enough mana for ${spell.name} (${spell.manaCost})" }

        val afterTurn1 = State(player1, boss1, effects1)
        if (afterTurn1.ends()) return afterTurn1

        // boss turn
        val turn2 = afterTurn1.applyEffects()
        if (turn2.ends()) return turn2

        val p2 = turn2.player
        return turn2.copy(
            player = p2.copy(hp = p2.hp - max(1, turn2.boss.damage - p2.armor))
        )
    }

    fun simulate(cost: Int = 0, hard: Boolean): Int {
        var minCost = 1_000_000
        if (cost >= 1242) return 999_999
        for (spell in spells) {
            try {
                val next = cast(spell, hard)
                val totalCost = cost + spell.manaCost
                if (next.playerWins()) {
                    minCost = min(minCost, totalCost)
                } else if (!next.playerLoses()) {
                    minCost = min(minCost, next.simulate(totalCost, hard))
                }
            } catch (e: Exception) {
//                println(e.message)
            }
        }
        if (minCost >= 1242) return 888_888
        return minCost
    }
}

fun main() {
    val input = readInput("Day${day}")
    val (hp, att) = input.map { it.split(": ").last().toInt() }
    val match = State(
        player = Character(50, mana = 500),
        boss = Character(hp, damage = att)
    )
    val part1 = match.simulate(hard = false)
    val part2 = match.simulate(hard = true)
    println(part1)
    println(part2)
    check(part2 in 1013 + 1 until 1242) { "Part2 solution is out of range" }
}

