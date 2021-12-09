package y16

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

private const val day = "01"

private data class Position(val x: Int, val y: Int, val dir: Pair<Int, Int>) {

    val distance = x.absoluteValue + y.absoluteValue

    override fun toString(): String {
        return "${x.toString().padStart(6)},${y.toString().padEnd(6)}"
    }

    operator fun rangeTo(p: Position): List<Pair<Int, Int>> {
        val xs = min(x, p.x)..max(x, p.x)
        val ys = min(y, p.y)..max(y, p.y)
        return xs.flatMap { xi ->
            ys.map { yi ->
                xi to yi
            }
        }
    }

    fun walk(rotate: String, length: Int): Position {
        val dir1 = if (rotate.startsWith("R")) {
            dir.second to -dir.first
        } else {
            -dir.second to dir.first
        }
        val x1 = x + dir1.first * length
        val y1 = y + dir1.second * length
        return Position(x1, y1, dir1)
    }
}

fun main() {
    fun List<String>.instructions() = joinToString("").split(", ")

    fun part1(input: List<String>) = input
        .instructions()
        .fold(Position(0, 0, 0 to +1)) { position, instruction ->
            position.walk(instruction.take(1), instruction.drop(1).toInt())
        }
        .distance

    fun part2(input: List<String>): Int {
        val beenThere = mutableSetOf(0 to 0)
        input.instructions()
            .fold(Position(0, 0, 0 to +1)) { position, instruction ->
                val destination = position.walk(instruction.take(1), instruction.drop(1).toInt())
                val locations = (position..destination) - (position.x to position.y)
                when (val visited = locations.firstOrNull { it in beenThere }) {
                    null -> beenThere.addAll(locations)
                    else -> return@part2 visited.first.absoluteValue + visited.second.absoluteValue

                }
                destination
            }
        return -1
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
