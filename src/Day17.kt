import kotlin.math.sign

private const val day = "17"

private data class Step(val x: Int = 0, val y: Int = 0, val vx: Int, val vy: Int) {
    fun inc() = Step(
        x = x + vx,
        y = y + vy,
        vx = vx - vx.sign,
        vy = vy - 1
    )
}

private data class Goal(val xRange: IntRange, val yRange: IntRange) {
    operator fun contains(step: Step) = step.x in xRange && step.y in yRange

    fun wasSkippedBy(step: Step) = step.y < yRange.first && step.vy < 0
}

private const val ARBITRARY_MAX_VY = 2_000

fun main() {
    fun String.toRange() = split("..").map { it.toInt() }.let { it[0]..it[1] }

    fun part1(input: List<String>): Int {
        val (xs, ys) = input.first().substringAfter(": x=").split(", y=")
        val goal = Goal(xRange = xs.toRange(), yRange = ys.toRange())
        return (0..goal.xRange.last).maxOf { vx ->
            (goal.yRange.first..ARBITRARY_MAX_VY).maxOf { vy ->
                val start = Step(vx = vx, vy = vy)
                val before = generateSequence(start) { it.inc() }
                    .takeWhile { !(it in goal || goal.wasSkippedBy(it)) }
                val endStep = before.lastOrNull()?.inc()
                if (endStep?.let<Step, Boolean> { it in goal } == true) {
                    before.maxOf { it.y }
                } else {
                    Int.MIN_VALUE
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val (xs, ys) = input.first().substringAfter(": x=").split(", y=")
        val goal = Goal(xRange = xs.toRange(), yRange = ys.toRange())
        return (0..goal.xRange.last).sumOf { vx ->
            (goal.yRange.first..ARBITRARY_MAX_VY).count { vy ->
                val start = Step(vx = vx, vy = vy)
                val before = generateSequence(start) { it.inc() }
                    .takeWhile { !(it in goal || goal.wasSkippedBy(it)) }
                val endStep = before.lastOrNull()?.inc()
                endStep?.let<Step, Boolean> { it in goal } == true
            }
        }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
