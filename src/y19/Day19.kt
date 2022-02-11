package y19

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val day = 19

fun main() {
    fun part1(codes: List<Long>) =
        (0 until 50).sumOf { x ->
            (0 until 50).sumOf { y ->
                val processor = IntCodeProcessor(codes)
                val result = AtomicInteger(0)
                processor.process(listOf(x.toLong(), y.toLong()).iterator()) { result.set(it.toInt()) }
                result.get()
            }
        }

    fun part2(codes: List<Long>): Int {
        fun isInRange(x: Int, y: Int): Boolean {
            val inputs = listOf(x.toLong(), y.toLong()).iterator()
            val bool = AtomicBoolean(false)
            IntCodeProcessor(codes).process(inputs) {
                bool.set(it > 0L)
            }
            return bool.get()
        }

        fun getRange(y: Int): IntRange {
            val xMin = y / 2
            val xMax = y * 3 / 4
            val x1 = (xMin..xMax).first { x -> isInRange(x, y) }
            val x2 = (xMax downTo x1).first { x -> isInRange(x, y) }
            return x1..x2
        }

        return (100..1500)
            .asSequence()
            .associateWith { getRange(it) }
            .filterValues { it.last - it.first > 100 }
            .toList()
            .windowed(100)
            .filter { it.last().first - it.first().first + 1 == 100 }
            .firstNotNullOf { list ->
                val map = list.toMap()
                val left = map.values.maxOf { it.first }
                val right = map.values.minOf { it.last }
                if (right - left < 99) null
                else 10000 * left + list.first().first
            }
    }

    val input = readInput("Day${day}").codes()
    println(part1(input))
    println(part2(input))
}
