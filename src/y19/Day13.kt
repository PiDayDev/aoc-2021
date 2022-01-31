package y19

private const val day = 13

fun main() {
    fun List<Long>.toTiles() =
        chunked(3).map { (a, b, c) -> Triple(a, b, c) }

    fun List<Triple<Long, Long, Long>>.score() =
        lastOrNull { it.first == -1L && it.second == 0L }?.third ?: 0

    fun printStatus(outputs: List<Long>) {
        val tiles = outputs.toTiles()
        val xs = 0L..tiles.maxOf { it.first }
        val ys = tiles.minOf { it.second }..tiles.maxOf { it.second }
        for (y in ys) {
            println(xs.joinToString("") { x ->
                when (tiles.firstOrNull { it.first == x && it.second == y }?.third ?: 0) {
                    1L -> "█" // a wall tile. Walls are indestructible barriers.
                    2L -> "░" // a block tile. Blocks can be broken by the ball.
                    3L -> "_" // a horizontal paddle tile. The paddle is indestructible.
                    4L -> "o" // a ball tile. The ball moves diagonally and bounces off objects.
                    else -> " " // an empty tile. No game object appears in this tile.
                }
            })
        }
        val score = tiles.score()
        val display = score.toString()
        val padding = "-".repeat(display.length)
        println(
            """
            +--$padding--+
            |  $display  |
            +--$padding--+
        """.trimIndent()
        )
    }

    fun extendPad(src: List<Long>): List<Long> {
        val searchZone = src.windowed(7).indexOf(listOf(0L, 0L, 0L, 3L, 0L, 0L, 0L))
        val start = src.take(searchZone).lastIndexOf(1L)
        val end = src.drop(searchZone).indexOf(1L) + searchZone
        return src.mapIndexed { j, v ->
            when {
                j < start -> v
                j > end -> v
                v != 0L -> v
                else -> 3L
            }
        }
    }

    fun part1(codes: List<Long>): Int {
        val outputs = mutableListOf<Long>()
        IntCodeProcessor(codes).process(iterator { }) { outputs += it }
        return outputs.chunked(3).count { (_, _, tile) -> tile == 2L }
    }

    fun List<Long>.play(): Long {
        val inputs = iterator {
            while (true) yield(0L)
        }
        val cheat = extendPad(this)
        val withQuarters = listOf(2L) + cheat.drop(1)
        val outputs = mutableListOf<Long>()
        val processor = IntCodeProcessor(withQuarters)
        while (!processor.halted()) {
            processor.process(inputs) { outputs += it }
            printStatus(outputs)
        }
        return outputs.toTiles().score()
    }

    fun part2(codes: List<Long>) = codes.play()

    val codes = readInput("Day$day").first().split(",").map { it.toLong() }
    println(part1(codes))
    println(part2(codes))
}
