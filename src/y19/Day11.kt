package y19

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

private const val day = 11

private typealias Panel = Pair<Int, Int>

private enum class PainterDirection(val dx: Int, val dy: Int) {
    UP(0, 1), RIGHT(1, 0), DOWN(0, -1), LEFT(-1, 0);

    fun turn(code: Long) = if (code == 0L) turnLeft() else turnRight()
    fun turnRight() = values()[(ordinal + 1) % values().size]
    fun turnLeft() = values()[(ordinal + 3) % values().size]
}

operator fun MutableMap<Panel, Long>.invoke(p: Panel) = this[p] ?: 0L
operator fun MutableMap<Panel, Long>.invoke(p: AtomicReference<Panel>) = this(p.get())

fun main() {
    fun paint(input: List<String>, initialColor: Long = 0L): Int {
        val hull = mutableMapOf((0 to 0) to initialColor)
        val colored = mutableSetOf<Panel>()
        val direction = AtomicReference(PainterDirection.UP)
        val position = AtomicReference(0 to 0)
        val inputs = iterator {
            while (true)
                yield(hull(position))
        }
        val nextOutputIsColor = AtomicBoolean(true)
        val outputs: (Long) -> Unit = {
            val coloring = nextOutputIsColor.get()
            if (coloring) {
                val where = position.get()
                val previousColor = hull(where)
                if (previousColor != it) {
                    colored += where
                }
                hull[where] = it
            } else {
                val d = direction.get().turn(it)
                val (x, y) = position.get()
                direction.set(d)
                position.set(x + d.dx to y + d.dy)
            }
            nextOutputIsColor.set(!coloring)
        }

        val codes = input.codes()
        val processor = IntCodeProcessor(codes)
        while (!processor.halted()) {
            processor.process(
                input = inputs,
                output = outputs
            )
        }
        val xs = hull.keys.map { (x, _) -> x }.sorted().let { it.first()..it.last() }
        val ys = hull.keys.map { (_, y) -> y }.sorted().let { it.first()..it.last() }

        ys.reversed().forEach { y ->
            println(xs.joinToString("") { x -> "${hull(x to y)}" }.replace('1', '█').replace('0', ' '))
        }
        return colored.size
    }

    fun part1(input: List<String>) = paint(input)

    fun part2(input: List<String>) = paint(input, 1L)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
