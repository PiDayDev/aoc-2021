package y19

private const val day = 24

private data class Bug(val x: Int, val y: Int, val z: Int)

fun main() {
    fun List<String>.gameOfBugs(): List<String> =
        mapIndexed { r, row ->
            row.mapIndexed { c, bug ->
                val bugs = listOfNotNull(
                    getOrNull(r)?.getOrNull(c + 1),
                    getOrNull(r)?.getOrNull(c - 1),
                    getOrNull(r + 1)?.getOrNull(c),
                    getOrNull(r - 1)?.getOrNull(c),
                ).count { it == '#' }
                when {
                    bug == '#' && bugs == 1 -> '#'
                    bug == '.' && bugs in 1..2 -> '#'
                    else -> '.'
                }
            }.joinToString("")
        }

    fun List<String>.biodiversity() =
        flatMap { it.toList() }.mapIndexed { index, c -> if (c == '#') 1 shl index else 0 }.sum()

    fun part1(input: List<String>) = generateSequence(input) { it.gameOfBugs() }
        .scan(listOf<List<String>>()) { acc, v -> acc + listOf(v) }
        .first { it.distinct().size < it.size }
        .last()
        .biodiversity()

    /*
     x 01234
    y
    0  #####
    1  #####
    2  ##?##
    3  #####
    4  #####
     */
    fun Bug.neighbors(): List<Bug> {
        val range = 0..4
        val sameLevel = listOf(copy(x = x + 1), copy(x = x - 1), copy(y = y - 1), copy(y = y + 1))
            .filter { it.x in range && it.y in range && !(it.x == 2 && it.y == 2) }
        val inner = when (x to y) {
            2 to 1 -> range.map { Bug(x = it, y = 0, z = z + 1) }
            2 to 3 -> range.map { Bug(x = it, y = 4, z = z + 1) }
            1 to 2 -> range.map { Bug(x = 0, y = it, z = z + 1) }
            3 to 2 -> range.map { Bug(x = 4, y = it, z = z + 1) }
            else -> listOf()
        }
        val outer1 = when (y) {
            0 -> Bug(x = 2, y = 1, z = z - 1)
            4 -> Bug(x = 2, y = 3, z = z - 1)
            else -> null
        }
        val outer2 = when (x) {
            0 -> Bug(x = 1, y = 2, z = z - 1)
            4 -> Bug(x = 3, y = 2, z = z - 1)
            else -> null
        }
        return sameLevel + inner + listOfNotNull(outer1, outer2)
    }

    fun Collection<Bug>.gameOfHyperBugs(): Collection<Bug> {
        return flatMap { it.neighbors() }
            .groupBy { it }
            .mapValues { (_, list) -> list.size }
            .filter { (bug, count) -> count == 1 || bug !in this && count == 2 }
            .keys
    }

    fun part2(input: List<String>): Int {
        val initial: Collection<Bug> = input.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, cell ->
                if (cell == '#') Bug(x, y, 0) else null
            }
        }
        return (1..200).fold(initial) { a, _ -> a.gameOfHyperBugs() }.size
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

