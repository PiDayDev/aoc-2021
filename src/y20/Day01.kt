package y20

private const val day = "01"

fun main() {
    fun part1(input: List<String>): Int {
        val numbers = input.map { it.toInt() }.sorted()
        val answer = numbers.first { (2020 - it) in numbers }
        return answer * (2020 - answer)
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map { it.toInt() }
        val pairs = numbers.flatMapIndexed { i, m ->
            numbers.drop(i).map { n ->
                m to n
            }
        }
        val (m, n) = pairs.first { (m, n) -> 2020 - m - n in numbers - m - n }
        return m * n * (2020 - m - n)
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 444019)
    val p2 = part2(input)
    println(p2)
    check(p2 == 29212176)
}
