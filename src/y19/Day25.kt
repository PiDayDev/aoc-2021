package y19

import y19.Direction25.*

private const val day = 25

private enum class Direction25(val s: String) {
    N("north"), S("south"), W("west"), E("east");

    override fun toString() = s
}

fun main() {
    fun String.ascii() = "$this\n".map { it.code.toLong() }

    fun wander(codes: List<Long>, steps: Int = 5000) {
        val droid = IntCodeProcessor(codes)
        val commands = (0..steps).asSequence()
            .map { Direction25.values().map { d -> "$d" }.shuffled()[it % 4] }
            .iterator()
        val inputs = iterator {
            while (true) {
                val command = commands.next()
                println("> $command")
                yieldAll(command.ascii())
            }
        }
        droid.process(inputs) {
            print(it.toInt().toChar())
        }
    }


    val codes = readInput("Day${day}").codes()

    fun part1(codes: List<Long>) {
        val droid = IntCodeProcessor(codes)

        val commands = listOf(
            N, W, W, "take spool of cat6",
            E, E, S, E, N, "take sand",
            W, N, "take jam",
            S, W, S, W, "take fuel cell",
            E, N, N, W, "inv",
            S
        )
            .map { it.toString() }
            .iterator()
        val inputs = iterator {
            while (commands.hasNext()) {
                val command = commands.next()
                println("> $command")
                yieldAll(command.ascii())
            }
        }
        droid.process(inputs) {
            print(it.toInt().toChar())
        }
    }
    /* // Uncomment this to explore the ship --> I used this to deduce map
     repeat(5) { wander(codes, 5000) }
 */
    part1(codes)
}
