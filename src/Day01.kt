fun main() {
    fun List<Int>.countIncreases() = windowed(2).count { it[1] > it[0] }

    fun part1(input: List<Int>) = input.countIncreases()

    fun part2(input: List<Int>) = input.windowed(3).map { it.sum() }.countIncreases()

    val input = readInput("Day01").map { it.toInt() }
    println(part1(input))
    println(part2(input))
}
