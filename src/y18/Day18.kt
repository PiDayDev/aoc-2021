package y18

private const val day = "18"

private const val OPEN = '.'
private const val TREE = '|'
private const val YARD = '#'

private fun List<List<Char>>.at(x: Int, y: Int) =
    getOrNull(y)?.getOrNull(x)

private fun List<List<Char>>.gameOfTrees() = mapIndexed { y, row ->
    row.mapIndexed { x, a ->
        val neighbors = listOfNotNull(
            at(x + 1, y - 1), at(x + 1, y), at(x + 1, y + 1), at(x - 1, y - 1),
            at(x - 1, y), at(x - 1, y + 1), at(x, y - 1), at(x, y + 1)
        )
        when (a) {
            OPEN -> if (neighbors.count { it == TREE } >= 3) TREE else a
            TREE -> if (neighbors.count { it == YARD } >= 3) YARD else a
            else -> if (neighbors.count { it == YARD } >= 1 && neighbors.count { it == TREE } >= 1) a else OPEN
        }
    }
}

private fun List<List<Char>>.count(c: Char) = sumOf { it.count { k -> k == c } }

private fun List<List<Char>>.value() = count(TREE) * count(YARD)

private fun List<Int>.period() = (1..size / 2).first { takeLast(it) == dropLast(it).takeLast(it) }

fun main() {

    fun part1(input: List<String>): Int =
        (1..10).fold(input.map { it.toList() }) { a, _ -> a.gameOfTrees() }.value()

    fun part2(input: List<String>): Int {
        val simulatedMinutes = 600
        val fields = (1..simulatedMinutes).asSequence()
            .scan(input.map { it.toList() }) { a, _ -> a.gameOfTrees() }
            .map { it.value() }
            .toList()

        val period = fields.period()
        val finalIndex = simulatedMinutes - period + (1000000000 - simulatedMinutes) % period

        return fields[finalIndex]
    }

    val input = readInput("Day${day}")
    println(part1(input))
    val a2 = part2(input)
    println(a2)
    check(a2 in 163898..172982)
}
