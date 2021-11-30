package y15

import java.io.FileNotFoundException
import kotlin.math.max

private const val day = "06"

private enum class Op {
    ON {
        override fun next(before: Boolean) = true
        override fun next(before: Int) = before + 1
    },
    OFF {
        override fun next(before: Boolean) = false
        override fun next(before: Int) = max(0, before - 1)
    },
    TOGGLE {
        override fun next(before: Boolean) = !before
        override fun next(before: Int) = before + 2
    };

    abstract fun next(before: Boolean): Boolean
    abstract fun next(before: Int): Int
}

private data class Instruction(val op: Op, val x: IntRange, val y: IntRange)

fun main() {
    val re = """^(t[\w ]+) (\d+),(\d+) through (\d+),(\d+)""".toRegex()

    fun moose(input: List<String>) = input

    fun parse(s: String): Instruction {
        val (instr, x0, y0, x1, y1) = re.matchEntire(s)?.destructured ?: throw IllegalArgumentException(s)
        val op = when (instr) {
            "turn on" -> Op.ON
            "turn off" -> Op.OFF
            else -> Op.TOGGLE
        }
        return Instruction(op, x0.toInt()..x1.toInt(), y0.toInt()..y1.toInt())
    }

    fun lights(grid: List<List<Boolean>>) = grid.sumOf { row -> row.count { it } }
    fun lights(grid: List<List<Int>>) = grid.sumOf { row -> row.sum() }

    fun part1(input: List<String>): Int {
        val grid = MutableList(1000) {
            MutableList(1000) { false }
        }
        input.forEach { row ->
            val instruction = parse(row)
            val (op, xRange, yRange) = instruction
            yRange.forEach { y ->
                xRange.forEach { x ->
                    val pre = grid[x][y]
                    grid[x][y] = op.next(pre)
                }
            }
        }

        return lights(grid)
    }

    fun part2(input: List<String>): Int {
        val grid = MutableList(1000) {
            MutableList(1000) { 0 }
        }
        moose(input).forEach { row ->
            val instruction = parse(row)
            val (op, xRange, yRange) = instruction
            yRange.forEach { y ->
                xRange.forEach { x ->
                    val pre = grid[x][y]
                    grid[x][y] = op.next(pre)
                }
            }
        }

        return lights(grid)
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 39)
    } catch (e: FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
