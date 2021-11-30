package y15

fun main() {
    fun part1(input: List<String>): Int {
        val (o, c) = input.first().partition { it == '(' }
        return o.length-c.length
    }

    fun part2(input: List<String>): Int {
        var f = 0
        input.first().forEachIndexed { index, c ->
            f += if (c=='(') +1 else -1
            if (f<0) return index+1
        }
        return -1
    }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
