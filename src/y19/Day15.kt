package y19

import kotlin.random.Random

private const val day = 15

private typealias Pos = Pair<Int, Int>

private val Pos.x
    get() = first
private val Pos.y
    get() = second

private operator fun Pos.plus(direction: Long) = when (direction) {
    1L -> copy(second = second + 1)
    2L -> copy(second = second - 1)
    3L -> copy(first = first - 1)
    4L -> copy(first = first + 1)
    else -> this
}

private const val NORTH = 1L
private const val SOUTH = 2L
private const val WEST = 3L
private const val EAST = 4L

private val directions = listOf(NORTH, SOUTH, WEST, EAST)

private fun randomCommand(list: List<Long>) = list[Random.Default.nextInt(list.size)]

fun main() {

    fun Map<Pos, Int>.getXs() =
        keys.toList().sortedBy { it.x }.let { it.first().x..it.last().x }

    fun Map<Pos, Int>.getYs() =
        keys.toList().sortedBy { it.y }.let { it.first().y..it.last().y }

    fun Map<Pos, Int>.isClosed(): Boolean {
        val open = filterValues { it > 0 }
        return open.keys.all { (x, y) ->
            val (top, bottom) = filterKeys { it.x == x }.toList().sortedBy { it.first.y }
                .let { it.first() to it.last() }
            val (left, right) = filterKeys { it.y == y }.toList().sortedBy { it.first.x }
                .let { it.first() to it.last() }
            listOf(top, bottom, left, right).all { it.second == 0 }
        }
    }

    fun Map<Pos, Int>.draw() {
        val origin = 0 to 0
        for (y in getYs()) {
            for (x in getXs()) {
                val pos = x to y
                if (pos == origin) print("R")
                else print(
                    when (this[pos] ?: -1) {
                        0 -> "|"
                        1 -> "."
                        2 -> "O"
                        else -> " "
                    }
                )
            }
            println()
        }
    }

    fun explore(codes: List<Long>): Map<Pos, Int> {
        val map = mutableMapOf((0 to 0) to 1)
        var position = 0 to 0
        var command = NORTH
        val robot = IntCodeProcessor(codes)
        val input = iterator {
            while (true) {
                val exploration = directions.filter { (position + it) !in map }
                val freeSpace = directions.filter { (map[position + it] ?: 666) > 0 }
                command = when {
                    exploration.isNotEmpty() -> randomCommand(exploration)
                    freeSpace.isNotEmpty() -> randomCommand(freeSpace)
                    else -> randomCommand(directions)
                }
                yield(command)
            }
        }

        var j = 0
        fun output(status: Long) {
            map[position + command] = status.toInt()
            if (++j % 50000 == 0 && map.isClosed())
                throw IllegalStateException("DONE")
            if (status > 0L)
                position += command
        }

        try {
            robot.process(input, ::output)
        } catch (e: IllegalStateException) {
            // ignored
        }

        return map
    }

    fun Collection<Pos>.minDistances(center: Pos): Map<Pos, Int> {
        val distances = mutableMapOf(center to 0)
        var last = distances.toMap()
        var d = 0
        while (last.isNotEmpty()) {
            d++
            val neighbors = last.keys
                .flatMap { p ->
                    directions.map { d -> p + d }
                }
                .distinct()
                .filter { it in this - distances.keys }
            last = neighbors.associateWith { d }
            distances.putAll(last)
        }
        return distances
    }


    fun part1(map: Map<Pos, Int>): Int {
        val goal = map.filterValues { it == 2 }.keys.first()
        val dist = map.filterValues { it > 0 }.keys.minDistances(0 to 0)
        return dist[goal]!!
    }

    fun part2(map: Map<Pos, Int>): Int {
        val goal = map.filterValues { it == 2 }.keys.first()
        val dist = map.filterValues { it > 0 }.keys.minDistances(goal)
        return dist.values.maxOf { it }
    }


    val codes = readInput("Day${day}").codes()

    val map = explore(codes)
    map.draw()
    println(part1(map))
    println(part2(map))
}
