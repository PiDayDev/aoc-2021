package y17

private const val day = 19

operator fun List<String>.get(rc: Pair<Int, Int>): Char = getOrNull(rc.first)?.getOrNull(rc.second) ?: ' '

operator fun Pair<Int, Int>.plus(p: Pair<Int, Int>) = first + p.first to second + p.second

fun main() {
    fun route(input: List<String>): MutableList<Char> {
        var pos = 0 to input.first().indexOf("|")
        var dir: Pair<Int, Int>? = +1 to 0
        val list = mutableListOf(input[pos])
        while (dir != null) {
            while (input[pos + dir] != ' ') {
                pos += dir
                list += input[pos]
            }
            dir = listOf(dir.second to dir.first, -dir.second to -dir.first).firstOrNull { input[pos + it] != ' ' }
        }
        return list
    }

    fun part1(input: List<String>) =
        route(input).filter { it !in "+-|" }.joinToString("")

    fun part2(input: List<String>) =
        route(input).size

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
