package y15

private const val day = "08"

fun main() {
    fun decode(s: String) = s.removeSurrounding("\"").replace("""\\(x..|\\|")""".toRegex()) { "#" }

    fun part1(input: List<String>) = input.sumOf {
        val c = decode(it)
        it.length - c.length
    }

    fun encode(s: String) = "#${s.replace("""["\\]""".toRegex()) { "##" }}#"

    fun part2(input: List<String>) = input.sumOf {
        val c = encode(it)
        c.length - it.length
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
