package y15

import kotlin.math.max
import kotlin.math.min

private const val day = "21"

private data class Stats(val hp: Int, val damage: Int, val armor: Int) {
    val isAlive = hp > 0
    infix fun attacks(s: Stats) = s.copy(hp = s.hp - max(1, damage - s.armor))
    operator fun plus(e: Equipment) = copy(damage = damage + e.damage, armor = armor + e.armor)
}

private data class Equipment(val cost: Int, val damage: Int, val armor: Int)

fun main() {

    val weapons = listOf(
        Equipment(8, 4, 0),
        Equipment(10, 5, 0),
        Equipment(25, 6, 0),
        Equipment(40, 7, 0),
        Equipment(74, 8, 0),
    )
    val armors = listOf(
        Equipment(13, 0, 1),
        Equipment(31, 0, 2),
        Equipment(53, 0, 3),
        Equipment(75, 0, 4),
        Equipment(102, 0, 5),
        Equipment(0, 0, 0)
    )
    val rings = listOf(
        Equipment(25, 1, 0),
        Equipment(50, 2, 0),
        Equipment(100, 3, 0),
        Equipment(20, 0, 1),
        Equipment(40, 0, 2),
        Equipment(80, 0, 3),
        Equipment(0, 0, 0),
        Equipment(0, 0, 0),
    )

    fun Pair<Stats, Stats>.fight(): Pair<Stats, Stats> {
        val (p1, p2) = this
        val bossAfter = p1 attacks p2
        val playerAfter = if (bossAfter.isAlive) p2 attacks p1 else p1
        return playerAfter to bossAfter
    }

    fun Pair<Stats, Stats>.playerWins(): Boolean {
        var match = this
        while (match.toList().all { it.isAlive }) {
            match = match.fight()
        }
        return match.first.isAlive
    }

    fun simulate(player: Stats, boss: Stats): Pair<Int, Int> {
        var minWinning = Int.MAX_VALUE
        var maxLosing = 0
        weapons.forEach { w ->
            armors.forEach { a ->
                for (i in rings.indices) {
                    val r1 = rings[i]
                    for (j in i + 1 until rings.size) {
                        val r2 = rings[j]
                        val cost = listOf(w, a, r1, r2).sumOf { it.cost }
                        if ((player + w + a + r1 + r2 to boss).playerWins())
                            minWinning = min(cost, minWinning)
                        else
                            maxLosing = max(cost, maxLosing)
                    }
                }
            }
        }
        return minWinning to maxLosing
    }

    val input = readInput("Day${day}")
    val (hp, att, def) = input.map { it.split(": ").last().toInt() }
    val boss = Stats(hp, att, def)
    val self = Stats(100, 0, 0)

    val (min, max) = simulate(self, boss)

    println(min)
    println(max)
}
