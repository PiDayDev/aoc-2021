private const val day = "11"

private const val JUST_FLASHED = 10

private const val PREVIOUSLY_FLASHED = 15

fun main() {
    fun List<List<Int>>.flashedNeighbors(r: Int, c: Int): Int =
        (-1..+1).flatMap { dr ->
            (-1..+1).map { dc ->
                getOrNull(r + dr)?.getOrNull(c + dc)
            }
        }.count { it == JUST_FLASHED }

    fun List<List<Int>>.chainReaction() = mapIndexed { r, row ->
        row.mapIndexed { c, value ->
            val chain = value + flashedNeighbors(r, c)
            when {
                chain < JUST_FLASHED -> chain
                value < JUST_FLASHED -> JUST_FLASHED
                else -> PREVIOUSLY_FLASHED
            }
        }
    }

    fun List<List<Int>>.increment() = map { row -> row.map { it + 1 } }

    fun List<List<Int>>.cleanUp() = map { row -> row.map { if (it == PREVIOUSLY_FLASHED) 0 else it } }

    fun List<List<Int>>.flash() =
        generateSequence(increment()) { it.chainReaction() }
            .dropWhile { it.any { row -> JUST_FLASHED in row } }
            .first()
            .cleanUp()

    fun parse(input: List<String>) = input.map { row -> row.split("").filter { it.isNotBlank() }.map { it.toInt() } }

    fun part1(input: List<String>) =
        generateSequence(parse(input)) { it.flash() }
            .drop(1)
            .take(100)
            .sumOf { grid -> grid.sumOf { row -> row.count { v -> v == 0 } } }

    fun part2(input: List<String>) =
        generateSequence(parse(input)) { it.flash() }
            .takeWhile { it.any { row -> row.sum() > 0 } }
            .count()

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
