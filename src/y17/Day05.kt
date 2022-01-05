package y17

private const val day = "05"

fun main() {
    fun List<String>.toOffsets() = map { it.toInt() }.toMutableList()

    fun part1(input: List<String>): Int {
        val list = input.toOffsets()
        var pointer = 0
        var steps = 0
        while (pointer in list.indices) {
            val d = list[pointer]
            list[pointer]++
            pointer += d
            steps++
        }
        return steps
    }

    fun part2(input: List<String>): Int {
        val list = input.toOffsets()
        var pointer = 0
        var steps = 0
        while (pointer in list.indices) {
            val d = list[pointer]
            if (d >= 3) list[pointer]-- else list[pointer]++
            pointer += d
            steps++
        }
        return steps
    }


    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
