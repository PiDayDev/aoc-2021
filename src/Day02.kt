private const val day = "02"

fun main() {
    operator fun Pair<Int, Int>.plus(p: Pair<Int, Int>) = first + p.first to second + p.second

    fun parse(row: String): Pair<String, Int> {
        val (direction, q) = row.split(" ")
        val quantity = q.toInt()
        return direction to quantity
    }

    fun movement(direction: String, quantity: Int) = when (direction) {
        "forward" -> +quantity to 0
        "down" -> 0 to +quantity
        "up" -> 0 to -quantity
        else -> throw RuntimeException()
    }

    fun part1(input: List<String>): Int {
        val (position, depth) = input
            .map { parse(it) }
            .fold(0 to 0) { location, (direction, quantity) ->
                location + movement(direction, quantity)
            }
        return position * depth
    }

    data class Coordinate(
        val aim: Int,
        val pos: Int,
        val depth: Int
    ) {
        fun plus(direction: String, quantity: Int): Coordinate {
            return when (direction) {
                "forward" -> copy(pos = pos + quantity, depth = depth + aim * quantity)
                "down" -> copy(aim = aim + quantity)
                "up" -> copy(aim = aim - quantity)
                else -> throw RuntimeException()
            }
        }

    }

    fun part2(input: List<String>): Int {
        val finalLocation = input
            .map { parse(it) }
            .fold(Coordinate(0, 0, 0)) { location, (direction, quantity) ->
                location.plus(direction, quantity)
            }
        return finalLocation.depth * finalLocation.pos
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
