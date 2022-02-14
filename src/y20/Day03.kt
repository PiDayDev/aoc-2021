package y20

private const val day = "03"

fun main() {
    fun List<String>.slope(dx: Int, dy: Int) = filterIndexed { y, row ->
        y % dy == 0 && row[(y * dx / dy) % row.length] == '#'
    }.size

    fun part1(input: List<String>) = input.slope(3, 1)

    fun part2(input: List<String>): Long = 1L *
            input.slope(1, 1) *
            input.slope(3, 1) *
            input.slope(5, 1) *
            input.slope(7, 1) *
            input.slope(1, 2)

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 240)
    val p2 = part2(input)
    println(p2)
    check(p2 == 2832009600L)
}
