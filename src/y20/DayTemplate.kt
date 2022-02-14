package y20

private const val day = "00"

fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 1)
    val p2 = part2(input)
    println(p2)
    check(p2 == 1)
}
