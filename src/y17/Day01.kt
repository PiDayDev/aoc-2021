package y17

private const val day = "01"

fun main() {
    fun part1(input: List<String>): Int {
        val code = input.first()
        return (code + code.first())
            .windowed(2)
            .filter { it[0] == it[1] }
            .sumOf { it[0].digitToInt() }
    }


    fun part2(input: List<String>): Int {
        val code = input.first()
        val len = code.length
        fun successor(index: Int) = code[(index + len / 2) % len]
        return code
            .filterIndexed { j, c -> c == successor(j) }
            .sumOf { it.digitToInt() }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
