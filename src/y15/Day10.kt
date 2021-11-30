package y15

fun main() {
    fun lookAndSay(s: String) = s
        .replace("""(.)(\1*)""".toRegex()) { m ->
            val k = m.groupValues[0]
            "${k.length}${k[0]}"
        }

    fun part1(input: String) = (1..40).fold(input) { acc, _ -> lookAndSay(acc) }.length

    fun part2(input: String) = (1..50).fold(input) { acc, _ -> lookAndSay(acc) }.length

    val input = "1113122113"
    println(part1(input))
    println(part2(input))
}
