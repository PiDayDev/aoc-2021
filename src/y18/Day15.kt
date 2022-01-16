package y18

private const val day = "15"

private fun Pair<Int, Int>.extend() = listOf(
    first to second - 1,
    first - 1 to second,
    first + 1 to second,
    first to second + 1,
)

private sealed class Critter(
    val name: String,
    val x: Int,
    val y: Int,
    val hp: Int = 200,
) : Comparable<Critter> {
    val pos = x to y
    override fun compareTo(other: Critter) =
        when (val dy = y - other.y) {
            0 -> x - other.x
            else -> dy
        }

    abstract fun move(xy: Pair<Int, Int>): Critter
    abstract fun hit(damage: Int): Critter
    override fun toString() = "${javaClass.simpleName.first()}'$name($hp)"

}

private class Elf(name: String, x: Int, y: Int, hp: Int = 200) : Critter(name, x, y, hp) {
    override fun move(xy: Pair<Int, Int>) = Elf(name, xy.first, xy.second, hp)
    override fun hit(damage: Int) = Elf(name, x, y, hp - damage)
}

private class Goblin(name: String, x: Int, y: Int, hp: Int = 200) : Critter(name, x, y, hp) {
    override fun move(xy: Pair<Int, Int>) = Goblin(name, xy.first, xy.second, hp)
    override fun hit(damage: Int) = Goblin(name, x, y, hp - damage)
}

private fun name(n: Int) = "$n"
    .mapIndexed { j, c ->
        c.digitToInt().let {
            when (j % 3) {
                0 -> "qwrtplkjhg"
                1 -> "aeiouyøüæœ"
                else -> "fdszxcvbnm"
            }[it]
        }
    }
    .joinToString("")
    .replaceFirstChar { it.uppercaseChar() }

private val idGen = generateSequence(100) { it + 29 }.map(::name).iterator()

private data class Map15(
    val map: List<String>,
    val critters: List<Critter>,
    val elfAttackPower: Int
) {
    constructor(input: List<String>, elfAttackPower: Int = 3) : this(
        map = input.map { it.replace('E', '.').replace('G', '.') },
        critters = input.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                when (c) {
                    'E' -> Elf(idGen.next(), x, y)
                    'G' -> Goblin(idGen.next(), x, y)
                    else -> null
                }
            }
        },
        elfAttackPower = elfAttackPower
    )

    fun getTargets(c: Critter) = when (c) {
        is Elf -> critters.filterIsInstance<Goblin>()
        is Goblin -> critters.filterIsInstance<Elf>()
    }

    fun getRange(c: Critter) = c.pos
        .extend()
        .filter { it in this }
        .filter { (critters - c).none { k -> k.x == it.first && k.y == it.second } }

    operator fun contains(xy: Pair<Int, Int>): Boolean {
        val (x, y) = xy
        return y in map.indices
                && x in map.first().indices
                && map[y][x] != '#'
    }

    fun isFree(xy: Pair<Int, Int>) = xy in this && critters.none { k -> k.x == xy.first && k.y == xy.second }

    fun reachable(c: Critter) = reachable(c.pos)

    fun reachable(xy: Pair<Int, Int>): Map<Pair<Int, Int>, Int> {
        val (x, y) = xy
        val distances = mutableMapOf((x to y) to 0)
        var last = distances.toMap()
        var d = 0
        while (last.isNotEmpty()) {
            d++
            val neighbors = last.keys.flatMap { it.extend() }
                .distinct()
                .filter { isFree(it) }
                .filter { it !in distances.keys }
            last = neighbors.associateWith { d }
            distances.putAll(last)
        }
        return distances
    }

    fun move(c: Critter): Pair<Map15, Critter>? {
        val targetRanges = getTargets(c).flatMap { getRange(it) }
        val reachable = reachable(c).filterKeys { it in targetRanges }
        val minDistance = reachable.minOfOrNull { it.value } ?: return null
        val destination = reachable
            .filterValues { it == minDistance }
            .keys
            .minWithOrNull(compareBy<Pair<Int, Int>> { it.second }.thenComparingInt { it.first })
            ?: return null

        val neighbors = c.pos.extend().filter { isFree(it) }
        val distancesFromTarget = reachable(destination)
        val movement = distancesFromTarget
            .filter { it.key in neighbors }
            .minWithOrNull(
                compareBy<Map.Entry<Pair<Int, Int>, Int>> { it.value }
                    .thenComparingInt { it.key.second }
                    .thenComparingInt { it.key.first }
            ) ?: return null

        val moved = c.move(movement.key)

        val result = copy(critters = critters - c + moved)
        return result to moved
    }

    fun attack(c: Critter): Pair<Map15, Critter>? {
        val range = c.pos.extend()
        val adversaries = getTargets(c).filter { it.pos in range }
        val minHp = adversaries.minOfOrNull { it.hp } ?: return null
        val adversary = adversaries.filter { it.hp == minHp }.minByOrNull { range.indexOf(it.pos) }!!
        val attack = if (c is Elf) elfAttackPower else 3
        val smashedAdversary = adversary.hit(attack)
        val critters1 = critters - adversary + (if (smashedAdversary.hp > 0) listOf(smashedAdversary) else emptyList())
        return copy(critters = critters1) to smashedAdversary
    }

    fun act(critter: Critter): Map15 {
        attack(critter)?.apply { return first }
        val (map1, c1) = move(critter) ?: (this to critter)
        return map1.attack(c1)?.first ?: map1
    }

    fun isBattleOver() = critters.partition { it is Elf }.toList().any { it.isEmpty() }

    fun outcome(completedTurns: Int) = completedTurns * critters.sumOf { it.hp }

    override fun toString(): String {
        val s = StringBuilder()
        map.forEachIndexed { y, row ->
            val suffix = StringBuilder()
            row.forEachIndexed { x, cell ->
                when (val who = critters.firstOrNull { it.x == x && it.y == y }) {
                    null -> s.append(cell)
                    else -> {
                        s.append(who.toString().first())
                        suffix.append(" | $who")
                    }
                }
            }
            s.append(suffix).appendLine()
        }
        return s.toString()
    }

}

fun main() {
    fun fullTurn(map: Map15): Pair<Map15, Boolean> {
        val critterIds = map.critters.sorted().map { it.name }
        return critterIds.foldIndexed(map to false) { index, (m, over), critterId ->
            val critter = m.critters.firstOrNull { it.name == critterId }
            if (critter == null) {
                m to false // it's dead, John
            } else {
                val step = m.act(critter)
                if (step.isBattleOver()) {
                    return step to (index < critterIds.indices.last)
                }
                step to over
            }
        }
    }

    fun fight(map: Map15, debug: Boolean = true): Pair<Map15, Int> {
        if (debug) println("Before battle:\n$map")
        generateSequence(1) { it + 1 }.fold(map) { m, turns ->
            val (next, prematureEnd) = fullTurn(m)
            val isOver = prematureEnd || next.isBattleOver()
            if (isOver) {
                if (debug) println("After $turns turns${if (prematureEnd) " (PREMATURE TURN END)" else ""}:\n$next")
                val completedTurns = turns - (if (prematureEnd) 1 else 0)
                return next to completedTurns
            }
            next
        }
        throw IllegalStateException("Could not find solution")
    }

    fun part1(input: List<String>): Int {
        val map = Map15(input)
        val (endMap, turns) = fight(map)
        return endMap.outcome(turns)
    }

    fun part2(input: List<String>): Int {
        val elfCount = Map15(input).critters.filterIsInstance<Elf>().size
        return (4..50).asSequence()
            .map { attack -> fight(Map15(input, elfAttackPower = attack)) }
            .filter { (m, _) -> m.critters.filterIsInstance<Elf>().size == elfCount }
            .map { (m, turns) -> m.outcome(turns) }
            .first()
    }

    val expectedTestResults = mapOf(
        0 to (27730 to 4988),
        1 to (36334 to -1),
        2 to (39514 to 31284),
        3 to (27755 to 3478),
        4 to (28944 to 6474),
        5 to (18740 to 1140)
    )
    val tests = readInput("Day${day}_test")
    expectedTestResults.forEach { (index, it) ->
        val (exp1, exp2) = it
        println("\n------ Test $index (expected result: $exp1 part1 / $exp2 part2) -----")
        val src = tests.dropWhile { it != "$index" }.drop(1).takeWhile { it.trim().isNotBlank() }
        val actual1 = part1(src)
        if (actual1 == exp1) {
            println(":: Test $index [PART 1] (o˘◡˘o) $actual1 IS RIGHT! (o˘◡˘o)\n\n")
        } else {
            println(":: Test $index [PART 1] (︶︹︶) Expected $it, not $actual1 (︶︹︶)\n\n")
            check(false)
        }
        if (exp2 > 0) {
            val actual2 = part2(src)
            if (actual2 == exp2) {
                println(":: Test $index [PART 2] (o˘◡˘o) $actual2 IS RIGHT! (o˘◡˘o)\n\n")
            } else {
                println(":: Test $index [PART 2] (︶︹︶) Expected $it, not $actual2 (︶︹︶)\n\n")
                check(false)
            }
        }
    }

    val input = readInput("Day${day}")
    println("\n-------------- PART 1 --------------")
    println(part1(input))

    println("\n-------------- PART 2 --------------")
    println(part2(input))
}
