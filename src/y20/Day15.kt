package y20

private const val day = "15"

fun main() {
    fun play(input: List<Int>, goal: Int): Int {
        val numberToTurn = input.mapIndexed { index, value -> value to index + 1 }
            .toMap().toMutableMap()
        var j = input.size
        var next = 0
        while (++j <= goal) {
            val pos = numberToTurn[next]
            val future = if (pos == null) 0 else j - pos
            numberToTurn[next] = j
            if (j == goal)
                return next
            next = future
        }
        return -1
    }

    fun part1(input: List<Int>) = play(input, 2020)

    fun part2(input: List<Int>) = play(input, 30000000)

    val input = readInput("Day$day").joinToString("").split(",").map { it.toInt() }
    val p1 = part1(input)
    println(p1)
    check(p1 == 1280)
    val p2 = part2(input)
    println(p2)
    check(p2 == 651639)
}
